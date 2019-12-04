package cn.com.geovis.datamigration.controller;


import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.domain.TaskStatus;
import cn.com.geovis.datamigration.etl.hbase2sqlite.H2Smigration;
import cn.com.geovis.datamigration.service.TaskService;
import cn.com.geovis.datamigration.vo.req.TaskReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;



@RestController
@RequestMapping("/task")
@Api(tags = "任务管理接口", description = "添加及查看任务功能")
public class TaskController extends BaseController {

    @Autowired
    public TaskService taskService;

    @ApiOperation(value = "新建任务", notes = "提交一个新的数据迁移任务")
    @RequestMapping(value = "/new", method = RequestMethod.PUT)
    public Response createTask(@RequestBody @Validated Task task, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return Response.error(1, generateErrorMessage(result));
        }
        Task.Result validator = task.validator();
        if (!validator.isOk()) {
            StringBuilder sb = new StringBuilder();
            for (String message : validator.getMessage()) {
                sb.append(message).append("\n");
            }
            return Response.error(500, sb.toString());
        }
        task.updateTaskStatus(TaskStatus.NEW);
        return taskService.createTask(task);
    }
    @ApiOperation(value = "修改任务状态为'暂停运行'", notes = "修改任务状态为'暂停运行'或者'继续运行'")
    @RequestMapping(value = "/updateStatus", method = RequestMethod.PUT)
    public Response upDatePauseStatus(int taskId){
        return taskService.updateTaskStatusById(taskId,TaskStatus.PAUSE);
    }
    @ApiOperation(value = "修改任务状态为'继续运行'", notes = "修改任务状态为'继续运行'")
    @RequestMapping(value = "/updateContinueStatus", method = RequestMethod.PUT)
    public Response upDateContinueStatus(int taskId){
        return taskService.updateTaskStatusById(taskId,TaskStatus.CONTINUE);
    }

    @ApiOperation(value = "根据taskId获取某一task详细信息", notes = "根据url的id来获取task详细信息")
    @RequestMapping(value = "/{id}", method = RequestMethod.GET)
    public Response getTaskById(@PathVariable int id) {
        return taskService.getTaskById(id);
    }

    @ApiOperation(value = "根获取所有的任务列表", notes = "分页获取任务列表")
    @RequestMapping(value = "/tasks", method = RequestMethod.GET)
    public Response getAllTask(TaskReq taskReq) {
        return taskService.getAllTask(taskReq);
    }


}
