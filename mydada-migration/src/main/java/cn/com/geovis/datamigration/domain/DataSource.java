package cn.com.geovis.datamigration.domain;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Setter;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;

import javax.validation.constraints.NotBlank;
import java.io.File;
import java.io.IOException;
import java.util.List;


@Data
@Setter
@ApiModel(value = "DataSource", description = "数据源传参格式")
public class DataSource {

    @ApiModelProperty(hidden = true)
    private int id;

    @ApiModelProperty(name = "type", value = "数据源类型", example = "1")
    private int type;

    @ApiModelProperty(name = "name",value = "数据源名称",example = "92集群")
    @NotBlank(message = "数据源名称不能为空")
    private String name;

    @ApiModelProperty(name = "name",value = "数据源地址",example = "192.168.4.92:2181")
    @NotBlank(message = "数据源地址不能为空")

    private String address;

    @ApiModelProperty(name = "item", value = "数据源配置项", hidden = true)
    private List<DatasourceItem> items;
    private boolean checkFileExist(String filePath) {

        File file = new File(filePath);
        return file.exists() && file.isDirectory();

    }

     private boolean checkTableExist(String zookeeperQuorum,String tableName) {
        //在 HBase 中管理、访问表需要先创建 HBaseAdmin 对象
        boolean exist = false;
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.quorum", zookeeperQuorum);

        try {

            Connection connection = ConnectionFactory.createConnection(conf);
            Admin admin = connection.getAdmin();
            exist = admin.tableExists(TableName.valueOf(tableName));

        } catch (IOException e) {
            e.printStackTrace();
        }

        return exist;
    }
    public boolean validator(DatasourceItem datasourceItem) throws Exception {
        int sourceType = type;
        String filePath = address + "/" + datasourceItem.getName();
        String zookeeperQuorum = address;
        String tableName = datasourceItem.getName();
        boolean flag = true;

        if (sourceType == 1) {

            if (!checkTableExist(zookeeperQuorum, tableName)) {
                flag = false;
            }

        }
        if (sourceType == 2) {

            if (!checkFileExist(filePath)) {
                flag = false;
            }

        }
        return flag;
    }

}
