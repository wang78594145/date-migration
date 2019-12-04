package cn.com.geovis.datamigration.scheduler;

import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.etl.hbase2sqlite.H2Smigration;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.MBTilesExcute;
import cn.com.geovis.datamigration.etl.sqlite2Sqlite.MBTilesFilter;
import cn.com.geovis.datamigration.service.DataSourceService;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.service.TaskService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Slf4j
@Component
public class TasksScheduler {

    @Autowired
    private TaskService taskService;
    @Autowired
    private H2Smigration h2sMigration;
    @Autowired
    private DataSourceService dataSourceService;
    @Autowired
    private MBTilesFilter mbTilesFilter;
    @Autowired
    private ITaskItemService iTaskItemService;

    @Autowired
    private MBTilesExcute mbTilesExcute;


    @Value("${limitTasksNum}")
    private int limitTasksNum;



    @Scheduled(initialDelay = 30000, fixedRate = 20000)
    @Async
    @Bean
    public void startH2STask() throws IOException, InterruptedException {
        //hbase2sqlite
        int limitNum;
        log.debug("定时器开始执行......");
        int runningTasksNum = taskService.getRunningTasksNum();//获取正在运行的任务数量
        limitNum = limitTasksNum - runningTasksNum > 0 ? (limitTasksNum - runningTasksNum) : 0;
        List<Task> tasksNew = taskService.getNewTasks(limitNum);
        for (Task task : tasksNew) {

            if (task.getSourceType() == 1) {
                h2sMigration.migrationH2S(task);
            }
            if (task.getSourceType()== 2){
                log.info("进入sqlite2sqlite");
                //当前无需要运行任务，方法返回
                mbTilesExcute.dispatchTask(task);
            }
        }
    }

}
