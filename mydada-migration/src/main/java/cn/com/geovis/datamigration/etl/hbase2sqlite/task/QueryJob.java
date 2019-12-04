package cn.com.geovis.datamigration.etl.hbase2sqlite.task;


import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.etl.hbase2sqlite.H2Smigration;
import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.ExportByLevel;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.service.TaskService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.CountDownLatch;


@Slf4j
public class QueryJob extends Thread {

    private ExportByLevel byLevel;
    private CountDownLatch countDownLatch;
    private int col;
    private int row;
    private TaskService taskService;
    private Task task;
    private ITaskItemService taskItemService;

    public QueryJob(ITaskItemService taskItemService,TaskService taskService, Task task, int col, int row, ExportByLevel byLevel, CountDownLatch countDownLatch) {
        this.taskItemService=taskItemService;
        this.task=task;
        this.taskService = taskService;
        this.byLevel = byLevel;
        this.countDownLatch = countDownLatch;
        this.col = col;
        this.row = row;
    }

    @Override
    public void run() {
        try {

            byLevel.queryBox(byLevel.getLevel(), col, row);
        } catch (Exception e) {
            taskService.updateStatus("运行失败",task.getId());
            log.error("Mbtiles:(" + byLevel.getLevel() + "," + col + "," + row + ")导出失败,程序准备退出");
            e.printStackTrace();
        } finally {
            this.countDownLatch.countDown();
        }
    }
}
