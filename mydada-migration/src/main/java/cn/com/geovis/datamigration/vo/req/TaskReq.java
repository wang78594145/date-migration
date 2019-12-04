package cn.com.geovis.datamigration.vo.req;


import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import java.sql.Date;

@Data
public class TaskReq {

    @ApiModelProperty(name = "page", value = "当前页码")
    private int page;

    @ApiModelProperty(name = "pageSize", value = "每页数据大小")
    private int pageSize;

    @ApiModelProperty(name = "status", value = "状态")
    private String status;

    @ApiModelProperty(name = "name", value = "任务名称")
    private String name;

    public void setStatus(String status) {
        if (status.equals("")) {
            this.status = null;
        } else {
            this.status = status;
        }
    }
}
