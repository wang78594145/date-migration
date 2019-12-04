package cn.com.geovis.datamigration.etl.hbase2sqlite;

import cn.com.geovis.coordinate.ICoordinateTransfer;
import cn.com.geovis.coordinate.impl.CoordinatTransefer3857;
import cn.com.geovis.coordinate.impl.CoordinateTransfer4326;
import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.ExportByLevel;
import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.Hbase;
import cn.com.geovis.datamigration.etl.hbase2sqlite.sqlite.SqliteDB;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.LayerProperties;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.service.TaskService;
import cn.com.geovis.tiles.IRowKeyConverter;
import cn.com.geovis.tiles.impl.RowKeyConverterV1;
import cn.com.geovis.tiles.impl.RowKeyConverterV2;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.*;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;


@Component
@Slf4j
@Data
public class H2Smigration {
    public static String baseDir;

    @Autowired
    private TaskService taskService;
    @Autowired
    private ITaskItemService taskItemService;

    private int taskTotal;
    private int zoom;
    private Table table;
    private String imageName;
    private String imageFormat;
    private byte typeCode;
    @Value("${h2SThreadCount}")
    private int threadCount;
    @Value("${h2SWriteCount}")
    private int writeCount;

    @Value("${isH2SBrokenPoint}")
    private boolean isH2SBrokenPoint;

    private double leftBottomLon;
    private double leftBottomLatitude;
    private double rightTopLon;
    private double rightTopLatitude;
    private String tableName;
    private int maxLevel;
    private String dirName;
    private String rootDir;
    private ICoordinateTransfer transfer;
    private IRowKeyConverter rowKeyConverter;
    public ExportByLevel exporter;
    private Map<Integer, List<String>> box = new HashMap<Integer, List<String>>();


    public void migrationH2S(Task task) {

        taskService.updateTaskStatusById(task.getId(), TaskStatus.RUNNING);//修改任务状态为正在运行
        if (task.getEpsg() == 3857) {
            this.transfer = new CoordinatTransefer3857();
        } else if (task.getEpsg() == 4326) {
            this.transfer = new CoordinateTransfer4326();
        }
        if (task.getRowkeyType() == 1) {
            this.rowKeyConverter = new RowKeyConverterV1();
        } else if (task.getRowkeyType() == 2) {
            this.rowKeyConverter = new RowKeyConverterV2();
        }

        //maxLevel<zoom
        zoom = task.getLayerMin() - 1;
        maxLevel = task.getLayerMax();
        rootDir = task.getOutputPath();
        imageName = task.getName();
        imageFormat = "IMAGE";
        typeCode = 0;//0表示光学彩色
        tableName = task.getSourceTablename();
        //需要判断范围，且left小于right
        leftBottomLon = task.getLonBegin();
        leftBottomLatitude = task.getLatBegin();
        rightTopLon = task.getLonEnd();
        rightTopLatitude = task.getLatEnd();
        if (leftBottomLatitude > rightTopLatitude || leftBottomLon > rightTopLon) {
            //备注中写入 error:左小经纬度不应大于右上经纬度
            taskService.updateComment("Error:左下经纬度不应大于右上经纬度", task.getId());
            //状态更新为运行失败
            taskService.updateTaskStatusById(task.getId(), TaskStatus.FAILED);//修改任务状态为正在运行
        } else {
            //初始化hbase连接
            Hbase hbase = new Hbase();
            //Source_address是zookeeper地址
            hbase.initConnection(task.getSourceAddress());
            table = hbase.getTable(tableName);
            SqliteDB.wal = "off";
            baseDir = rootDir + "/";
            taskTotal = 0;
            while (++zoom <= maxLevel) {
                dirName = baseDir + zoom;
                generateBaseDir(dirName);
                generatePair(zoom);
            }
            taskService.updateTaskTotal(task.getId(), taskTotal);
            int taskDoneItemCount = taskItemService.getTaskItemCountById(task.getId());
            taskService.updateProgress(taskTotal, taskDoneItemCount, task.getId());
            zoom = task.getLayerMin() - 1;
            while (++zoom <= maxLevel) {
                //根据要导出图层级别初始化HbaseExportByLevel实例，并开始进行数据导出
                exporter = new ExportByLevel(zoom, table,
                        imageName, imageFormat, typeCode,
                        threadCount, writeCount,
                        leftBottomLon, leftBottomLatitude, rightTopLon, rightTopLatitude, task, taskService, taskItemService, transfer, rowKeyConverter);
                exporter.setBox(box);
                exporter.setH2SBrokenPoint(isH2SBrokenPoint);
                exporter.exportData(task);
            }

        }
    }

    //生成图层level文件夹
    private static void generateBaseDir(String dirName) {
        File file = new File(dirName);
        if (file.exists()) {
            log.info("文件目录" + dirName + "已存在");
        } else {
            try {
                file.mkdirs();
            } catch (Exception e) {
                log.info("创建目录失败");
            }
        }
    }

    //统计每层任务数
    private void generatePair(int level) {
        //按照图层左上角col,row信息设置起始box坐标
        List<String> list = new ArrayList<>();

        int startColumn = transfer.generateColumnAndRow(leftBottomLatitude, leftBottomLon, level)[0];
        int startRow = transfer.generateColumnAndRow(leftBottomLatitude, leftBottomLon, level)[1];
        int maxColumn = transfer.generateColumnAndRow(rightTopLatitude, rightTopLon, level)[0];
        int maxRow = transfer.generateColumnAndRow(rightTopLatitude, rightTopLon, level)[1];
        int col = (startColumn / 250) * 250;
        int row = (startRow / 250) * 250;
        int beginRow = (startRow / 250) * 250;
        while (col <= maxColumn) {
            while (row <= maxRow) {
                list.add(col + "_" + row);
                taskTotal++;
                row += 250;
            }
            row = beginRow;
            col += 250;
        }
        box.put(level, list);
    }


}

