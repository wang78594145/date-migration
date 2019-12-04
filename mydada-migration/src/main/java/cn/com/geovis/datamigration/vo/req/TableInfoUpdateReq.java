package cn.com.geovis.datamigration.vo.req;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author wangqianyi
 * @Title: TableInfoUpdateReq
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/22 15:45
 */

@Data
@ApiModel
public class TableInfoUpdateReq {

    @ApiModelProperty(name = "id",value = "所属集群id")
    private int id;

    @ApiModelProperty(name = "name",value = "表名称")
    private String name;

    @ApiModelProperty(name = "comment",value = "表中文名称")
    private String comment;
}
