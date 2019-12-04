package cn.com.geovis.datamigration.vo.req;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
@ApiModel
public class TableInfoReq {

    @ApiModelProperty(name = "dsId",value = "所属集群id")
    private int dsId;

    @ApiModelProperty(name = "name",value = "表名称")
    private String name;

    @ApiModelProperty(name = "comment",value = "表中文名称")
    private String comment;

    @ApiModelProperty(name = "page",value = "当前页码")
    private int page;

    @ApiModelProperty(name = "pageSize",value = "每页大小")
    private int pageSize;
}
