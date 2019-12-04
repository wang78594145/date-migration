package cn.com.geovis.datamigration.service.impl;

import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.mapper.TaskMapper;
import cn.com.geovis.datamigration.service.TaskService;
import cn.com.geovis.datamigration.vo.req.TaskReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service("taskService")
public class TaskServiceImpl implements TaskService {
    private final TaskMapper taskMapper;


    @Autowired
    public TaskServiceImpl(TaskMapper taskMapper) {
        this.taskMapper = taskMapper;
    }

    @Override
    public Response createTask(Task task) {
        try {
            taskMapper.createTask(task);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "创建任务失败");
        }
    }


    @Override
    /**
     * 分页查询
     *
     * @param page     当前页数
     * @param pageSize 每页个数
     * @return
     */

    public Response getTaskById(int id) {
        Task task = taskMapper.getTaskById(id);
        return Response.success(task);
    }


    public Response getAllTask(TaskReq taskReq) {
        PageHelper.startPage(taskReq.getPage(), taskReq.getPageSize());
        List<Task> tasks = taskMapper.getAllTask(taskReq);
        PageInfo<Task> info = new PageInfo<>(tasks);
        return Response.success(info);
    }


    @Override
    public List<Task> getNewTasks(int limitNum) {
        List<Task> tasksNew = taskMapper.getNewTask(limitNum);
        return tasksNew;
    }

    @Override
    public boolean updateStatus(String status, int id) {
        taskMapper.updateStatus(status, id);
        return true;
    }

    @Override
    public boolean updateProgress(int taskTotal, int taskDone, int id) {
        short progress = 0;
        if (taskTotal != 0) {
            progress = (short) (100 * taskDone / taskTotal);
        } else if (taskTotal == 0) {
            progress = 0;
        }
        taskMapper.updateProgress(taskTotal, taskDone, progress, id);
        return true;
    }


    public boolean updateComment(String comments, int id) {
        taskMapper.updateCommet(comments, id);
        return true;
    }


    @Override
    public Response getTaskCountByStatus(String taskStatus) {
        try {
            int count = taskMapper.getTaskCountByStatus(taskStatus);
            return Response.success(count);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "查询失败");
        }
    }


    @Override
    public Response getTaskByStatus(String taskStatus) {
        try {
            List<Task> data = taskMapper.getTaskByStatus(taskStatus);
            return Response.success(data);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "查询失败");
        }
    }


    @Override
    public Response updateTaskStatusById(int id, TaskStatus taskStatus) {
        try {
            taskMapper.updateStatusById(id, taskStatus.getName());
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }
    }

    @Override
    public Response updateTaskTotal(int id, int taskNum) {
        try {
            taskMapper.updateTaskTotalById(id, taskNum);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }
    }


    @Override
    public Response updateFinishTime(int id, LocalDateTime finishTime) {
        try {
            taskMapper.updateFinishTimeById(id, finishTime);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }
    }

    @Override
    public Response updateProgress(int id, short progress) {
        try {
            taskMapper.updateProgressById(id, progress);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }
    }

    @Override
    public Response updateTaskDone(int id, int taskDoneNum) {
        try {
            taskMapper.updateTaskDoneById(id, taskDoneNum);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "更新任务状态失败");
        }
    }


    @Override
    public int getRunningTasksNum(){
        int runningTasks=taskMapper.getRunningTasksNum();
        return runningTasks;
    }
}
