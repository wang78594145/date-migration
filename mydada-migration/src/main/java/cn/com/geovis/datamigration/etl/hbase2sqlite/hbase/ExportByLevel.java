package cn.com.geovis.datamigration.etl.hbase2sqlite.hbase;


import cn.com.geovis.coordinate.ICoordinateTransfer;
import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.etl.hbase2sqlite.sqlite.SqliteDB;
import cn.com.geovis.datamigration.etl.hbase2sqlite.task.QueryJob;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.LayerProperties;
import cn.com.geovis.datamigration.service.TaskService;
import cn.com.geovis.tiles.IRowKeyConverter;
import cn.com.geovis.datamigration.service.ITaskItemService;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;


import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * 使用方法：
 * 1. 在主函数中生成新的ExportByLevel类的实例。
 * 2. 使用类实例调用exportData()方法。
 **/

/**
 * 类工作概述：
 * 1. 构造函数初始化阶段：
 * a. 初始化线程池。
 * b. 初始化导出记录列表。
 * c. 计算抽取层级的lcr边界。对应方法：setStartColumn();setStartRow();setMaxColumn();setMaxRow();
 * 2. exportData()方法调用阶段。
 * a. 根据lcr边界，生成该区域内所有Mbtiles的原始边界列表。对应方法：generatePair()
 * b. 根据Mbtiles的原始边界列表的大小，生成一个该层级的CountDownLatch实例。
 * b. 对原始索引列表进行遍历，为每个Mbtiles生成一个迁移线程任务。见：exportData()方法。
 * c. 使用CountDownLatch实例对主线程阻塞，等待主线程结束。
 * d. 在日志表中记录该层级，代表该层级数据已经全部导出。
 */


@Slf4j
public class ExportByLevel {

    private int level;
    private int startRow;
    private int startColumn;
    private int maxRow;
    private int maxColumn;
    private int typeCode;
    private double leftBottomLon;
    private double leftBottomLatitude;
    private double rightTopLon;
    private double rightTopLatitude;
    private Table table;
    private String imageName;
    private String imageFormat;
    private ExecutorService JobPool;
    private int writeCount;
    private TaskService taskService;
    private ITaskItemService taskItemService;
    private Task task;
    private ICoordinateTransfer transfer;
    private IRowKeyConverter rowKeyConverter;
    private Boolean isH2SBrokenPoint;
    private Map<Integer, List<String>> box = new HashMap<Integer, List<String>>();

    public ExportByLevel(int level, Table table,
                         String imageName, String imageFormat, int code, int threadCount, int writeCount,
                         double leftBottomLon, double leftBottomLatitude,
                         double rightTopLon, double rightTopLatitude, Task task, TaskService taskService, ITaskItemService taskItemService, ICoordinateTransfer transfer, IRowKeyConverter rowKeyConverter) {
        this.level = level;
        this.table = table;
        this.imageName = imageName;
        this.imageFormat = imageFormat;
        this.typeCode = code;
        this.JobPool = Executors.newFixedThreadPool(threadCount);
        this.writeCount = writeCount;
        this.leftBottomLon = leftBottomLon;
        this.leftBottomLatitude = leftBottomLatitude;
        this.rightTopLon = rightTopLon;
        this.rightTopLatitude = rightTopLatitude;
        this.taskService = taskService;
        this.taskItemService = taskItemService;
        this.task = task;
        this.transfer = transfer;
        this.rowKeyConverter = rowKeyConverter;
        setStartColumn();
        setStartRow();
        setMaxColumn();
        setMaxRow();

        log.info("图层的lcr范围[左下: (" + this.level + "," + startColumn + "," + startRow + "),右上: (" + this.level + "," + maxColumn + "," + maxRow + ")");
    }

    //检测暂停运行功能
    private boolean pauseTask() {
        Task t = (Task) taskService.getTaskById(task.getId()).getData();
        String status = t.getStatus();
        if (status.equals("暂停运行")) {
            return false;
        }
        return true;
    }

    public void exportData(Task task) {
        log.info("原本层级任务数量" + box.get(level).size());
        generatePair();
        List<String> list = box.get(level);
        log.info("本层级剩下任务" + list.size());
        CountDownLatch countDownLatch = new CountDownLatch(list.size());
        ExportByLevel byLevel = this;
        for (String pair : list) {
            if (!pauseTask()) {
                log.info("正在暂停" + task.getName() + "任务");
                return;
            }
            JobPool.execute(new QueryJob(taskItemService, taskService, task,
                    Integer.parseInt(pair.split("_")[0]), Integer.parseInt(pair.split("_")[1]), byLevel, countDownLatch));
        }
        list.clear();
        box.get(level).clear();
        try {
            countDownLatch.await();
            log.info("图层[左下: (" + this.level + "," + startColumn + "," + startRow + "),右上: (" + this.level + "," + maxColumn + "," + maxRow + ")]" + "数据导出结束");
        } catch (InterruptedException e) {
            taskService.updateStatus("运行失败", task.getId());
            e.printStackTrace();
        }
    }

    //读取一个box
    public void queryBox(int level, int col, int row) throws SQLException, ClassNotFoundException, IOException {
        int endIndex = col + 250;
        int index = col > startColumn ? col : startColumn;
        SqliteDB db = new SqliteDB(level, col, row, imageName, imageFormat, writeCount);
        while (index < endIndex && index <= maxColumn) {
            if (index < startColumn) {
                continue;
            }
            queryByColumn(index++, row, db);
        }
        db.commitDataAndCloseDB();
        taskItemService.createItem(task.getId(), level + "_" + col + "_" + row, "运行成功");
        int taskDoneCount = taskItemService.getTaskItemCountById(task.getId());
        Task t = (Task) taskService.getTaskById(task.getId()).getData();
        int taskTotal = t.getTaskTotal();
        taskService.updateTaskDone(task.getId(), taskDoneCount);
        taskService.updateProgress(taskTotal, taskDoneCount, task.getId());
        if (taskDoneCount == taskTotal) {
            taskService.updateStatus("运行成功", task.getId());
            taskService.updateFinishTime(task.getId(), LocalDateTime.now());
            task.setFinishTime(LocalDateTime.now());
            LayerProperties layerProperties = new LayerProperties();
            layerProperties.writeLayerPropertiesFile(task);//写layerproperties文件
            log.info("图层" + imageName + "导出成功");
        }
    }

    //按照给定column读取一列瓦片数据
    private void queryByColumn(int col, int beginIndex, SqliteDB db) throws SQLException, IOException {
        List<Get> getList = new ArrayList<Get>();
        int endIndex = beginIndex + 250;
        int index = beginIndex > startRow ? beginIndex : startRow;

        while (index < endIndex && index <= maxRow) {
            if (index < startRow) {
                continue;
            }
            getList.add(new Get(this.rowKeyConverter.generateRowkey(level, col, index++, task.getPreCode())));
        }


        Result[] results;
        results = table.get(getList);

        if (results.length > 0) {
            for (Result result : results) {
                if (result.getRow() != null) {
                    int[] lcr = this.rowKeyConverter.generateLCR(result.getRow());
                    byte[] img = result.getValue("ti".getBytes(), "i".getBytes());
                    db.insertData(lcr, img);
                }
            }
        }
    }

    //生成图层包含的所有box的(level,col,row)对应的实例，并加入到list中
    private void generatePair() {
        if (!isH2SBrokenPoint) return;
        List<String> successNameList = taskItemService.getSuccessNameById(task.getId());
        for (String successName : successNameList) {
            String l = successName.split("_")[0];
            if (l.equals(String.valueOf(level))) {
                String c = successName.split("_")[1];
                String r = successName.split("_")[2];
                if (box.get(level).contains(c + "_" + r)) {
                    box.get(level).remove(c + "_" + r);
                }
            }
        }
    }


    public int getLevel() {
        return level;
    }

    public void setStartRow() {
        this.startRow = this.transfer.generateColumnAndRow(leftBottomLatitude, leftBottomLon, level)[1];
    }

    private void setStartColumn() {
        this.startColumn = this.transfer.generateColumnAndRow(leftBottomLatitude, leftBottomLon, level)[0];
    }

    private void setMaxRow() {
        this.maxRow = this.transfer.generateColumnAndRow(rightTopLatitude, rightTopLon, level)[1];
    }

    private void setMaxColumn() {
        this.maxColumn = this.transfer.generateColumnAndRow(rightTopLatitude, rightTopLon, level)[0];
    }

    public void setBox(Map<Integer, List<String>> box) {
        this.box = box;
    }

    public void setH2SBrokenPoint(Boolean h2SBrokenPoint) {
        isH2SBrokenPoint = h2SBrokenPoint;
    }

    public Boolean getH2SBrokenPoint() {
        return isH2SBrokenPoint;
    }


}
