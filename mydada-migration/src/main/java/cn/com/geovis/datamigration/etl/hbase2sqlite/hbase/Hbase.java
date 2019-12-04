package cn.com.geovis.datamigration.etl.hbase2sqlite.hbase;

import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.io.compress.Compression;
import org.apache.hadoop.hbase.regionserver.BloomType;

import java.io.IOException;


@Slf4j
public class Hbase {

    private static Configuration configuration;
    private static Connection connection;
    private int  hbaseWriteBufferSize;

    public void initConnection(String zookeeperSetting,int hbaseWriteBufferSize,String tableName,String... columnFamily) {
        if (zookeeperSetting.length() == 0) {
            zookeeperSetting = "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181";
        }
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zookeeperSetting);
        this.hbaseWriteBufferSize=hbaseWriteBufferSize;
        try {
            connection = ConnectionFactory.createConnection(configuration);
            createTable(tableName,columnFamily);
        } catch (IOException e) {
            log.error("获取hbase数据库连接失败");
            e.printStackTrace();
        }
    }


    public void initConnection(String zookeeperSetting) {
        if (zookeeperSetting.length() == 0) {
            zookeeperSetting = "zookeeper1:2181,zookeeper2:2181,zookeeper3:2181";
        }
        configuration = HBaseConfiguration.create();
        configuration.set("hbase.zookeeper.quorum", zookeeperSetting);
        try {
            connection = ConnectionFactory.createConnection(configuration);
        } catch (IOException e) {
            log.error("获取hbase数据库连接失败");
            e.printStackTrace();
        }
    }

    public Table getTable(String tableName) {
        Table table = null;
        try {
            table = connection.getTable(TableName.valueOf(tableName));
            table.setWriteBufferSize(hbaseWriteBufferSize);
            ((HTable)table).setAutoFlush(false);
        } catch (IOException e) {
            log.error("获取Hbase表： " + tableName + " 失败");
            e.printStackTrace();
        }
        return table;
    }

    private void createTable(String tableName,String... columnFamily){
        try {
            Admin admin = connection.getAdmin();
            if (!admin.tableExists(TableName.valueOf(tableName))){
                HTableDescriptor tableDescriptor = new HTableDescriptor(TableName.valueOf(tableName));
                tableDescriptor.setMaxFileSize(1024*1024*1024);
                tableDescriptor.setMemStoreFlushSize(1024*1024*256);
                for (String cf : columnFamily) {
                    HColumnDescriptor coldesc = new HColumnDescriptor(cf);
                    coldesc.setBlocksize(1024 * 1024 * 1); //1M
                    coldesc.setMaxVersions(999);
                    coldesc.setBloomFilterType(BloomType.ROW);
                    coldesc.setCompressionType(Compression.Algorithm.SNAPPY);
                    tableDescriptor.addFamily(coldesc);
                }
                admin.createTable(tableDescriptor);
            }
        } catch (IOException e) {
            log.error("创建表失败");
            e.printStackTrace();
        }
    }

    public void closeConnection(){
        try {
            connection.close();
        } catch (IOException e) {
            log.error("关闭Hbase连接失败");
            e.printStackTrace();
        }
    }


}
