
package cn.com.geovis.datamigration.etl.sqlite2Sqlite;


import cn.com.geovis.coordinate.ICoordinateTransfer;
import cn.com.geovis.coordinate.impl.CoordinatTransefer3857;
import cn.com.geovis.coordinate.impl.CoordinateTransfer4326;
import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskItem;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.service.DataSourceService;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.service.TaskService;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


@Slf4j
@Data
@Component("mbTilesExcute")
public class MBTilesExcute {

    private DataSourceService dataSourceService;

    private TaskService taskService;

    private MBTilesFilter mbTilesFilter;

    private ITaskItemService iTaskItemService;

    @Value("${s2SthreadNum}")
    private int s2SthreadNum;

    @Value("${ifBreakPointResumeOn}")
    private boolean ifBreakPointResumeOn ;

    @Autowired
    public MBTilesExcute(DataSourceService dataSourceService,
                         TaskService taskService,
                         MBTilesFilter mbTilesFilter,
                         ITaskItemService iTaskItemService) {
        this.dataSourceService = dataSourceService;
        this.taskService = taskService;
        this.mbTilesFilter = mbTilesFilter;
        this.iTaskItemService = iTaskItemService;
    }

    //获取数据库中任务状态为新建的需要执行的任务列表
    public List<Task> getTasks() {
        List<Task> taskList = (List<Task>) taskService.getTaskByStatus("新建").getData();
        return taskList;
    }

    //检查任务表中给定的输入路径是否真实存在
    private boolean checkFileExist(File file) {
        return file.exists() && file.isDirectory();
    }
    private ICoordinateTransfer getEPSGFormat(Task task) {
        int epsg = task.getEpsg();
        ICoordinateTransfer iCoordinateTransfer = null;
        if (epsg == 4326) {
            iCoordinateTransfer = new CoordinateTransfer4326();
        } else if (epsg == 3857)
            iCoordinateTransfer = new CoordinatTransefer3857();
        return iCoordinateTransfer;
    }

    //执行待执行的任务
    private ArrayList<String> getTilesToExcute(Task task) {
        ArrayList<String> tiles = new ArrayList<>();
        String filePath = task.getSourceAddress()+"/"+task.getSourceTablename();
        File file = new File(filePath);
        if (checkFileExist(file)) {
            if (task.getLatBegin() != task.getLatEnd() && task.getLonBegin() != task.getLonEnd()) {
                log.info("开始执行任务" + task.getName());
                //为mbTilesFilter实例赋通用值
                mbTilesFilter.setTransfer(getEPSGFormat(task))
                        .setInputPath(filePath)
                        .setOutputPath(task.getOutputPath())
                        .setLatitudeBegin(task.getLatBegin())
                        .setLatitudeEnd(task.getLatEnd())
                        .setLonEnd(task.getLonEnd())
                        .setLonBegin(task.getLonBegin());
                for (int i = task.getLayerMin(); i <= task.getLayerMax(); i++) {
                    mbTilesFilter.setLevel(i);
                    mbTilesFilter.setColumnAndRow();
                    String path = mbTilesFilter.getOutputPath() + "/" + i;
                    if (!new File(path).exists()) {
                        new File(path).mkdirs();
                    }
                    mbTilesFilter.getValidTiles(tiles, mbTilesFilter.getInputPath() + "/" + i);
                }

            } else {
                log.info("开始点和结束点相同");
            }
        } else {
            //update tasks  status logs="输出路径有问题"
            log.info("sqlitePath:" + filePath + "不存在，执行下一个任务");
            taskService.updateTaskStatusById(task.getId(), TaskStatus.FAILED);
        }
        if (this.ifBreakPointResumeOn) {
            breakpoint(tiles, task.getId());
        }
        return tiles;
    }

    //拼接目标sqlite绝对路径
    private String spliceTileOutputPath(String sourcePath) {
        String s = mbTilesFilter.getOutputPath();
        List dd = Arrays.asList(sourcePath.split("/"));
        String output = s + "/" + dd.get(dd.size() - 2) + "/" + dd.get(dd.size() - 1);
        return output;
    }

    private void upDateTaskProgress(Task task, ArrayList<String> tiles) {
        int taskDoneCount =  iTaskItemService.getTaskItemCountById(task.getId());
        taskService.updateTaskDone(task.getId(), taskDoneCount);
        short rate = (short) (taskDoneCount * 10000 / tiles.size() / 100);
        taskService.updateProgress(task.getId(), rate);
    }

    private void upDateTaskFinalStatus(Task task, ArrayList<String> tiles, TaskStatus taskStatus) {
        upDateTaskProgress(task, tiles);
        taskService.updateTaskStatusById(task.getId(), taskStatus);
        taskService.updateFinishTime(task.getId(), LocalDateTime.now());
    }

    //将sourcePath对应的文件拷贝output
    private boolean copyFiles(String sourcePath, String output) throws IOException {
        return mbTilesFilter.copyFileUsingFileChannels(new File(sourcePath), new File(output));

    }

    //检测暂停运行功能
    private boolean pauseTask(Task task){
        Task t =(Task) taskService.getTaskById(task.getId()).getData();
        String status =t.getStatus();
        if (status.equals("暂停运行")){
            return false;
        }
        return true;
    }
    //多线程分发子任务方法
    private void doTask(Task task, ArrayList<String> tiles, int threadNum) {
        //todo 更新task。1 task_total 。2.status
        taskService.updateTaskTotal(task.getId(), tiles.size());
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(threadNum);
        CountDownLatch countDownLatch = new CountDownLatch(tiles.size());
        long startTime = System.currentTimeMillis();
        for (int j = 0; j < tiles.size(); j++) {
            if (!pauseTask(task)){
                log.info("正在暂停"+task.getName()+"任务");
                return;
            }
            String sourcePath = tiles.get(j);
            String output = spliceTileOutputPath(sourcePath);
            fixedThreadPool.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        boolean success = copyFiles(sourcePath, output);
                        if (success) {

                            TaskItem taskItem = new TaskItem(task.getId(),
                                    sourcePath, TaskStatus.SUCCESS.getName());

                            iTaskItemService.insertTaskItem(taskItem);
                            //todo 更新task_item.
                        } else {
                            TaskItem taskItem = new TaskItem(task.getId(), sourcePath,
                                    TaskStatus.FAILED.getName());
                            iTaskItemService.insertTaskItem(taskItem);
                            log.info(sourcePath + "位置的数据迁移失败");
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        upDateTaskProgress(task, tiles);
                        countDownLatch.countDown();//工人完成工作，计数器减一
                    }
                }
            });
        }

        //等待子线程全部结束后更新统计状态
        try {
            countDownLatch.await();
            log.info("共耗时:" + (System.currentTimeMillis() - startTime) / 1000.0 + "s");

            //查表统计更新taskdone
            int taskDoneCount =  iTaskItemService.getTaskItemCountById(task.getId());
            if (taskDoneCount == tiles.size()) {
                upDateTaskFinalStatus(task, tiles, TaskStatus.SUCCESS);
                LayerProperties layerProperties = new LayerProperties();
                layerProperties.writeLayerPropertiesFile(task);//写layerproperties文件
            } else {
                upDateTaskFinalStatus(task, tiles, TaskStatus.FAILED);
                LayerProperties layerProperties = new LayerProperties();
                layerProperties.writeLayerPropertiesFile(task);//写layerproperties文件
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //按id有选择性的执行新建任务
    private List<Task> getPreTask(List<Task> tasks, List<Integer> taskIds) {
        Map<Integer, Task> map = new HashMap<Integer, Task>();
        List<Task> preTasks = new ArrayList<>();
        for (Task subtask : tasks) {
            map.put(subtask.getId(), subtask);
        }
        for (int taskId : taskIds) {
            if (map.containsKey(taskId)) {
                preTasks.add(map.get(taskId));
            }
        }
        return preTasks;
    }

    private List<String> breakpoint(List<String> tiles, int taskId) {

        List<String> successfulPaths = (List<String>) iTaskItemService.getSuccessNameById(taskId);
        for (String path : successfulPaths) {
            if (tiles.contains(path)) {
                tiles.remove(path);
            }
        }
        return tiles;
    }



    public void dispatchTask(Task task) {
        taskService.updateTaskStatusById(task.getId(), TaskStatus.RUNNING);//修改任务状态为正在运行
        ArrayList<String> tiles = getTilesToExcute(task); //获取该任务所有并且真实存在的需要迁移的文件名
        if (tiles.size() != 0) {
            doTask(task, tiles,s2SthreadNum );//分发子任务多线程执行
        } else {
            taskService.updateTaskStatusById(task.getId(), TaskStatus.FAILED);
            log.info("该给定范围内不存在mbtile文件,进行下一个任务");
        }
    }

}



