package cn.com.geovis.datamigration.domain;


import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author wangqianyi
 * @Title: Task
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/20 10:23
 */

@Data
@Accessors(chain = true)
@Api(value = "任务实体")
public class Task {

    @ApiModelProperty(hidden = true)
    private int id;

    @ApiModelProperty(name = "name", value = "任务名称", example = "中国区数据导出任务")
    @NotBlank(message = "任务名称不能为空")
    private String name;


    @ApiModelProperty(hidden = true)
    private String status;

    @ApiModelProperty(hidden = true)
    private LocalDateTime createTime;

    @ApiModelProperty(hidden = true)
    private LocalDateTime finishTime;

    @ApiModelProperty(name = "latBegin", value = "120")
    @Min(value = -90, message = "纬度最小值为-90")
    @Max(value = 90, message = "纬度最大值为90")
    private double latBegin;

    @ApiModelProperty(value = "结束维度")
    @Min(value = -90, message = "纬度最小值为-90")
    @Max(value = 90, message = "纬度最大值为90")
    private double latEnd;

    @ApiModelProperty(value = "数据源类型")
    private int sourceType;

    @ApiModelProperty(value = "数据源地址")
    private String sourceAddress;

    @ApiModelProperty(value = "源表名称")
    private String sourceTablename;

    @ApiModelProperty(value = "起始经度")
    @Min(value = -180, message = "经度最小值为-180")
    @Max(value = 180, message = "经度最大值为180")
    private double lonBegin;

    @ApiModelProperty(value = "结束经度")
    @Min(value = -180, message = "经度最小值为-180")
    @Max(value = 180, message = "经度最大值为180")
    private double lonEnd;

    @ApiModelProperty(value = "输出格式", hidden = true)
    private int outputFormat;

    @ApiModelProperty(value = "输出路径")
    @NotBlank(message = "任务输路径不能为空")
    private String outputPath;

    @ApiModelProperty(value = "起始层级")
    @Min(0)
    @Max(23)
    private short layerMin;

    @ApiModelProperty(value = "结束层级")
    @Min(0)
    @Max(23)
    private short layerMax;

    @ApiModelProperty(hidden = true)
    private int taskTotal;

    @ApiModelProperty(hidden = true)
    private int taskDone;

    @ApiModelProperty(hidden = true)
    private short progress;

    @ApiModelProperty(value = "备注")
    private String comments;

    @ApiModelProperty(value = "EPSG坐标系类型")
    private int epsg;

    @ApiModelProperty(value = "瓦片数据前缀（如66）")
    private int preCode;

    @ApiModelProperty(value = "rowkey类型")
    private  int rowkeyType;

    public void updateTaskStatus(TaskStatus status) {
        this.status = status.getName();
    }


    private boolean checkFileExist() {

        File file = new File(sourceAddress);
        return file.exists() && file.isDirectory();
    }

     boolean checkTableExist() {
        //在 HBase 中管理、访问表需要先创建 HBaseAdmin 对象
        boolean exist = false;
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", sourceAddress);
        try {
            Connection connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            exist = admin.tableExists(TableName.valueOf(sourceTablename));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return exist;
    }

    public Result validator() throws Exception {

        Result result = new Result();
        result.setOk(true);
        if (latEnd <= latBegin) {
            result.setMessage("起始纬度不能大于结束纬度");
            result.setOk(false);
            throw new Exception("起始纬度不能大于结束纬度");
        }
        if (layerMax < layerMin) {
            result.setMessage("起始级别不能大于结束级别");
            result.setOk(false);
            throw new Exception("起始级别不能大于结束级别");
        }

        if (lonEnd <= lonBegin) {
            result.setMessage("起始经度不能大于结束纬度");
            result.setOk(false);
            throw new Exception("起始经度不能大于结束纬度");
        }
        if (sourceType == 1) {

            if (!checkTableExist()) {
                result.setMessage("HBase源表名称不存在");
                result.setOk(false);
                throw new Exception("HBase源表名称不存在");
            }
        }
        if (sourceType == 2) {
            if (!checkFileExist()) {

                result.setMessage("数据源地址不是有效路径");
                result.setOk(false);
                throw new Exception("数据源地址不是有效路径");
            }
        }

        return result;
    }

    public static class Result {
        private List<String> message = new ArrayList<>();
        private boolean isOk;

        public List<String> getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message.add(message);
        }

        public boolean isOk() {
            return isOk;
        }

        public void setOk(boolean ok) {
            isOk = ok;
        }
    }
}
