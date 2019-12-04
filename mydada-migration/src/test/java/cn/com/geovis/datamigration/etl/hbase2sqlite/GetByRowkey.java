package cn.com.geovis.datamigration.etl.hbase2sqlite;

import cn.com.geovis.coordinate.ICoordinateTransfer;
import cn.com.geovis.coordinate.impl.CoordinatTransefer3857;
import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.Hbase;
import cn.com.geovis.tiles.IRowKeyConverter;
import cn.com.geovis.tiles.impl.RowKeyConverterV2;
import org.apache.hadoop.hbase.client.*;
import org.apache.hadoop.hbase.util.Bytes;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Iterator;

public class GetByRowkey {
    private ICoordinateTransfer transfer;
    private IRowKeyConverter rowKeyConverter;


    @Test
    public void getByRowkeyTest() throws IOException {
        this.transfer = new CoordinatTransefer3857();
        this.rowKeyConverter = new RowKeyConverterV2();
        String tableName = "tile:globe_3857_L13_14";
        Table table;
        Hbase hbase = new Hbase();
        //Source_address是zookeeper地址
        hbase.initConnection("192.168.48.32:2181,192.168.48.30:2181,192.168.48.31:2181");
        table = hbase.getTable(tableName);
        //测试使用
        Scan scan = new Scan();
        scan.setMaxResultSize(12);
        Get get = new Get(this.rowKeyConverter.generateRowkey(13, 0, 0, 1001));
        Result result1 = table.get(get);
//        System.out.println("row = " + Arrays.toString(this.rowKeyConverter.generateLCR(result1.getRow())));
        byte[] value1 = result1.getValue(Bytes.toBytes("ti"), Bytes.toBytes("i"));
//        System.out.println("value = " + Bytes.toString(value1));

        ResultScanner resultScanner = table.getScanner(scan);
        Iterator<Result> results = resultScanner.iterator();
        Result result;
        String rowString;
        int count = 0;
        File file;
        FileOutputStream fos;
        while (results.hasNext()) {
            if (count++ == 10) break;
            result = results.next();
            byte[] row = result.getRow();
            long rowkey = Bytes.toLong(row);
            rowString = Arrays.toString(this.rowKeyConverter.generateLCR(row));
            byte[] value = result.getValue("ti".getBytes(), "i".getBytes());
            file = new File("/home/fwk/" + count + ".jpg");
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
            fos = new FileOutputStream(file);
            fos.write(value);
        }
    }
}
