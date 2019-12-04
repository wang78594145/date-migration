package cn.com.geovis.datamigration;

import cn.com.geovis.coordinate.ICoordinateTransfer;
import cn.com.geovis.coordinate.impl.CoordinatTransefer3857;
import cn.com.geovis.coordinate.impl.CoordinateTransfer4326;
import cn.com.geovis.datamigration.domain.TaskItem;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.etl.hbase2sqlite.H2Smigration;
import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.ExportByLevel;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.LayerProperties;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.MBTilesFilter;
import cn.com.geovis.datamigration.mapper.DataSourceMapper;
import cn.com.geovis.datamigration.mapper.DatasourceItemMapper;
import cn.com.geovis.datamigration.mapper.TaskMapper;
import cn.com.geovis.datamigration.service.DataSourceService;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.service.impl.DataSourceServiceImpl;
import cn.com.geovis.datamigration.service.impl.TaskServiceImpl;
import cn.com.geovis.datamigration.service.TaskService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import java.time.LocalDateTime;
import java.util.List;


@RunWith(SpringRunner.class)
@SpringBootTest(classes = {DataMigrationApplication.class})
public class DataMigrationApplicationTests {

    @Value("${s2SthreadNum}")
    private String num;
    @Autowired
    TaskService taskService;
    @Autowired
    ITaskItemService iTaskItemService;
    DataSourceMapper dataSourceMapper;
    DatasourceItemMapper datasourceItemMapper;
    TaskMapper taskMapper;
    @Autowired
    ExportByLevel exportByLevel;
    @Autowired
    H2Smigration h2Smigration;

    @Test
    public void upTaskStatusTest() {

        taskService.updateTaskStatusById(7, TaskStatus.RUNNING);
    }

    @Test
    public void upTaseTotalTest() {
        taskService.updateTaskTotal(7, 10);
    }

    @Test
    public void upDateTaskDoneByIdTest() {
        taskService.updateTaskDone(7, 19);

    }

    @Test
    public void upDateTaskFinishTimeTest() {

        taskService.updateFinishTime(7, LocalDateTime.now());

    }

    @Test
    public void addTaskItemTest() {
        TaskItem taskItem = new TaskItem();

        taskItem.setName("123");
        taskItem.setStatus(TaskStatus.SUCCESS.getName());

//        taskService.addTaskItem(taskItem);


    }

    @Test
    public void updateProgress() {
        taskService.updateProgress(7, (short) 100);
    }

    @Test
    public void getSuccessPathByIdTest() {
        int taskId = 44;
        List<String> list = iTaskItemService.getSuccessNameById(taskId);
        for (String l : list) {
            System.out.println(l);
        }
    }

    @Test
    public void sqliteTosqlite() {
        DataSourceService dataSourceService = new DataSourceServiceImpl(dataSourceMapper, datasourceItemMapper);
        TaskService taskService = new TaskServiceImpl(taskMapper);
//        MBTilesExcute mbTilesExcute=new MBTilesExcute(dataSourceService, iTaskService, mbTilesFilter);
        MBTilesFilter mbTilesFilter = new MBTilesFilter();
        mbTilesFilter.setTransfer(new CoordinatTransefer3857());
        mbTilesFilter.setLatitudeBegin(17.29657);
        mbTilesFilter.setLonBegin(69.807968);
        mbTilesFilter.setLatitudeEnd(55.86819);
        mbTilesFilter.setLonEnd(138.714218);
        mbTilesFilter.setLevel(16);
        mbTilesFilter.setColumnAndRow();
        System.out.println("StartColumn:" + mbTilesFilter.getStartColumn());
        System.out.println("StartRow:" + mbTilesFilter.getStartRow());
        System.out.println("MaxColumn:" + mbTilesFilter.getMaxColumn());
        System.out.println("MaxRow:" + mbTilesFilter.getMaxRow());


        System.out.println("StartColumn:" + mbTilesFilter.getStartColumn());
        System.out.println("StartRow:" + mbTilesFilter.getStartRow());
        System.out.println("MaxColumn:" + mbTilesFilter.getMaxColumn());
        System.out.println("MaxRow:" + mbTilesFilter.getMaxRow());
    }

    @Test
    public void testCo() {
        ICoordinateTransfer transfer = new CoordinateTransfer4326();
        int[] start = transfer.generateColumnAndRow(20.902429, -168.777187, 9);
        int[] end = transfer.generateColumnAndRow(71.22403, -32.898281, 9);
        System.out.println(start[0] + "," + start[1]);
        System.out.println(end[0] + "," + end[1]);
    }

    @Test
    public void testCountByStatus() {
        int doingTask = (int) taskService.getTaskCountByStatus("正在运行").getData();
        System.out.println(doingTask);
    }

    @Test
    public void layerProtiesTest() {
        System.out.println("EPSG\\:");
    }

    @Test
    public void testLayerProperties(){
        LayerProperties layerProperties =new LayerProperties();
        double[] bbox = new double[]{-180,-90,180,90};
        double minX = layerProperties.transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[0];
        double minY = layerProperties.transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[1];
        double maxX = layerProperties.transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[2];
        double maxY = layerProperties.transformCRSs(bbox, "EPSG:4326", "EPSG:3857")[3];
        String bounds = String.format("%.6f", minX) + "," + String.format("%.6f", minY )
                + "," + String.format("%.6f", maxX) + "," + String.format("%.6f", maxY );
        System.out.println(bounds);

    }

    @Test
    public void getRunningTaskNum() {
        int runningTasksNum = taskService.getRunningTasksNum();
        System.out.println(runningTasksNum);


    }
@Test
    public void getTaskNumByStatus(){
        int num= (int) taskService.getTaskCountByStatus("正在运行").getData();
        System.out.println(num);
    }


    @Test
    public void test222(){
//        System.out.println(num);
//        System.out.println("ThreadCount:"+h2Smigration.getThreadCount());
//        System.out.println("WriteCount:"+h2Smigration.getWriteCount());
        System.out.println(exportByLevel.getH2SBrokenPoint());
    }

    @Test
    public void upDateTaskStatus(){

    }

}
