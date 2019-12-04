package cn.com.geovis.datamigration.domain;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@Api(value = "任务Item实体")
public class TaskItem {
    @ApiModelProperty(hidden = true)
    private int taskId;
    private String name;
    private String status;

    public TaskItem(int taskId, String name, String status) {
        this.taskId = taskId;
        this.name = name;
        this.status = status;
    }

    public TaskItem() {
    }
}
