package cn.com.geovis.datamigration.etl.sqlite2Sqlite;

import cn.com.geovis.coordinate.ICoordinateTransfer;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

@Slf4j
@Data
@Component
@Accessors(chain = true)
public class MBTilesFilter {

    private int level;
    private int startRow;
    private int startColumn;
    private int maxRow;
    private int maxColumn;
    private double latitudeBegin;
    private double LonBegin;
    private double latitudeEnd;
    private double lonEnd;
    private String inputPath; //绝对路径 /home/test
    private String outputPath;//绝对路径 /home/test
    private ICoordinateTransfer transfer;


    //得到需要copy的sqlite文件的绝对路径
    public ArrayList<String> getValidTiles(ArrayList<String> exists, String path0) {

        int col = (startColumn / 250) * 250;
        int row = (startRow / 250) * 250;
        int beginRow = (startRow / 250) * 250;
        String name = null;
        while (col <= maxColumn) {
            while (row <= maxRow) {
                name = path0 + "/tiles_" + col + "_" + row + ".sqlite";
                if (new File(name).exists()) {
                    exists.add(name);
                } else {
                    log.info(name + "输出路径不存在");
                }
                row += 250;
            }
            row = beginRow;
            col += 250;
        }
        return exists;
    }

    //FileChannels 用来copy数据。
    boolean copyFileUsingFileChannels(File source, File dest) throws IOException {
        boolean success = false;
        if (source.exists()) {
            FileChannel inputChannel = null;
            FileChannel outputChannel = null;
            try {
                inputChannel = new FileInputStream(source).getChannel();
                outputChannel = new FileOutputStream(dest).getChannel();
                outputChannel.transferFrom(inputChannel, 0, inputChannel.size());
                success = true;
            } finally {
                inputChannel.close();
                outputChannel.close();
            }
        }
        return success;
    }

    public void setColumnAndRow() {

        int[] startColAndRow = transfer.generateColumnAndRow(latitudeBegin, LonBegin, level);
        this.startColumn = startColAndRow[0];
        this.startRow = startColAndRow[1];
        int[] maxColAndRow = transfer.generateColumnAndRow(latitudeEnd, lonEnd, level);
        this.maxColumn = maxColAndRow[0];
        this.maxRow = maxColAndRow[1];

    }
}
