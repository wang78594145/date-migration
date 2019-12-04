package cn.com.geovis.datamigration.service;

import cn.com.geovis.datamigration.domain.TaskItem;
import cn.com.geovis.datamigration.vo.resp.Response;

import java.util.List;

public interface ITaskItemService {

    Response insertTaskItem(TaskItem taskItem);

    int getTaskItemCountById(int tasksId);

    List<String> getSuccessNameById(int taskId);

    boolean createItem(int task_id, String name, String status);

}
