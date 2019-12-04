package cn.com.geovis.datamigration.domain;

import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

@Data
public class TableInfo {

    @ApiModelProperty(hidden = true)
    private int id;

    @ApiModelProperty(name = "dsId", value = "数据源id", example = "1", hidden = true)
    private int dsId;

    @ApiModelProperty(name = "name", value = "表英文名称", example = "global_base")
    private String name;

    @ApiModelProperty(name = "name", value = "表中文解释", example = "全球底图")
    private String comment;
}
