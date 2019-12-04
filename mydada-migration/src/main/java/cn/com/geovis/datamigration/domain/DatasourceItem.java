package cn.com.geovis.datamigration.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @author wangqianyi
 * @Title: DatasourceItem
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/19 19:29
 */

@Data
@ApiModel
public class DatasourceItem {


    @ApiModelProperty(name="id",value = "主键",hidden = true)
    private int id;

    @ApiModelProperty(name="dsId",value = "数据源id",example = "1")
    @NotNull
    private int dsId;

    @ApiModelProperty(name="name",value = "表英文名称",example = "global_base")
    @NotBlank(message = "表名称不能为空")
    private String name;

    @ApiModelProperty(name = "name", value = "表中文解释", example = "全球底图")
    private String comment;

}
