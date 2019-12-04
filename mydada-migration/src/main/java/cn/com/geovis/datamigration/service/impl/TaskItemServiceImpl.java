package cn.com.geovis.datamigration.service.impl;

import cn.com.geovis.datamigration.domain.TaskItem;
import cn.com.geovis.datamigration.mapper.TaskItemMapper;
import cn.com.geovis.datamigration.service.ITaskItemService;
import cn.com.geovis.datamigration.vo.resp.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("taskItemService")
public class TaskItemServiceImpl implements ITaskItemService {

    private final TaskItemMapper taskItemMapper;

    @Autowired
    public TaskItemServiceImpl(TaskItemMapper taskItemMapper) {
        this.taskItemMapper = taskItemMapper;
    }

    @Override
    public Response insertTaskItem(TaskItem taskItem) {

        try {
            taskItemMapper.insertTaskItem(taskItem);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }

    }

    @Override
    public int getTaskItemCountById(int tasksId) {

            int count = taskItemMapper.getTaskItemCountById(tasksId);
            return count;
    }

    @Override
    public List<String> getSuccessNameById(int taskId) {
            List<String> successfulPath = taskItemMapper.getSuccessNameById(taskId);
            return successfulPath;
    }

    @Override
    public  boolean createItem(int task_id,String name,String status ){
        taskItemMapper.createItem(task_id,name,status);
        return true;
    }



}
