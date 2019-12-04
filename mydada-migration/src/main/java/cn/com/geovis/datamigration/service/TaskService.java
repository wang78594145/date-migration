package cn.com.geovis.datamigration.service;

import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.vo.req.TaskReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import java.util.List;
import java.time.LocalDateTime;


public interface TaskService {
    Response createTask(Task task);

    Response getTaskById(int id);

    Response getTaskByStatus(String taskStatus);

    Response getTaskCountByStatus(String taskStatus);

    Response updateTaskStatusById(int id, TaskStatus taskStatus);

    Response updateTaskTotal(int id, int taskNum);

    Response updateFinishTime(int id, LocalDateTime finishTime);

    Response updateProgress(int id, short progress);

    Response updateTaskDone(int id, int taskDoneNum);

    Response getAllTask(TaskReq taskReq);

    List<Task> getNewTasks(int limitNum);

    boolean updateStatus(String status, int id);

    boolean updateProgress(int taskTotal, int taskDone, int id);

    int getRunningTasksNum();

    boolean updateComment(String comments, int id);




}
