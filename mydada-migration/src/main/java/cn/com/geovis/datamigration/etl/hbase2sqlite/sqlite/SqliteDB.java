package cn.com.geovis.datamigration.etl.hbase2sqlite.sqlite;


import cn.com.geovis.datamigration.etl.hbase2sqlite.H2Smigration;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
public class SqliteDB {

    private static int ERROR_CODE = 1;
    private static int FIRST_INDEX;
    private static int SECOND_INDEX;
    private static int THIRD_INDEX;
    private static int FORTH_INDEX;
    public static String wal;

    private static String sqlCreateTilesTable;
    private static String sqlCreateTilesMetadata;
    private static String sqlCreateMetadata;
    private static String insertMetadataSql;
    private static String insertSql;

    static {
        sqlCreateTilesTable = "CREATE TABLE IF NOT EXISTS tiles (zoom_level integer, tile_column integer, tile_row integer, tile_data blob,  CONSTRAINT pk_tiles PRIMARY KEY(zoom_level, tile_column,tile_row));";
        sqlCreateTilesMetadata = "CREATE TABLE IF NOT EXISTS tiles_metadata (zoom_level integer, tile_column integer, tile_row integer, create_time integer, CONSTRAINT pk_tiles PRIMARY KEY(zoom_level, tile_column,tile_row));";
        sqlCreateMetadata = "CREATE TABLE IF NOT EXISTS metadata (name text, value text, CONSTRAINT pk_metadata PRIMARY KEY(name));";
        insertMetadataSql = "INSERT OR REPLACE INTO metadata  (name,value) values (?,?)";
        insertSql = "INSERT OR REPLACE INTO tiles  (zoom_level, tile_column, tile_row , tile_data) values (?,?,?,?)";
        FIRST_INDEX = 1;
        SECOND_INDEX = 2;
        THIRD_INDEX = 3;
        FORTH_INDEX = 4;
    }

    private Connection conn;
    private PreparedStatement preparedStatement;
    private int count = 1;
    private int writeCount;
    private int level;
    private int col;
    private int row;

    public SqliteDB(int level, int col, int row, String imageName, String imageFormat, int writeCount) throws SQLException, ClassNotFoundException {
        this.writeCount = writeCount;
        this.level = level;
        this.col = col;
        this.row = row;
        String dbName = "tiles_" + col + "_" + row;
        Class.forName("org.sqlite.JDBC");
        this.conn = DriverManager.getConnection("jdbc:sqlite:" + H2Smigration.baseDir + level + "/" + dbName + ".sqlite");

        Statement stmt = conn.createStatement();

        //生成tiles表
        stmt.executeUpdate(sqlCreateTilesTable);

        //生成tiles_metadata表
        stmt.executeUpdate(sqlCreateTilesMetadata);

        //生成metadata表
        stmt.executeUpdate(sqlCreateMetadata);
        stmt.close();

        //向metadata表插入相关数据
        initMetadata(imageName, imageFormat);

        conn.setAutoCommit(false);
        preparedStatement = conn.prepareStatement(insertSql);
    }

    private void initMetadata(String imageName, String imageFormat) throws SQLException {
        PreparedStatement pst = conn.prepareStatement(insertMetadataSql);
        Map<String, String> dataMap = new HashMap<String, String>();
        dataMap.put("name", imageName);
        dataMap.put("format", imageFormat);
        dataMap.put("minzoom", "" + level);
        dataMap.put("maxzoom", "" + level);

        Set<Map.Entry<String, String>> dataSet = dataMap.entrySet();
        for (Map.Entry<String, String> entry : dataSet) {
            pst.setString(FIRST_INDEX, entry.getKey());
            pst.setString(SECOND_INDEX, entry.getValue());
            pst.addBatch();
        }
        pst.executeBatch();
        pst.close();
    }

    public void insertData(int lcr[], byte[] img) throws SQLException {
        preparedStatement.setInt(FIRST_INDEX, lcr[0]);
        preparedStatement.setInt(SECOND_INDEX, lcr[1]);
        preparedStatement.setInt(THIRD_INDEX, lcr[2]);
        preparedStatement.setBytes(FORTH_INDEX, img);
        preparedStatement.addBatch();
        if (count++ >= writeCount) {
            commitData();
            count=1;
        }

    }

    private void commitData() throws SQLException {
        preparedStatement.executeBatch();
        conn.commit();
    }

    private void closeDB() {
        try {
            preparedStatement.close();
            conn.close();
        } catch (SQLException e) {
            log.error("数据库连接关闭失败");
        }
    }

    public void commitDataAndCloseDB() throws SQLException {
        commitData();
        closeDB();
    }
}
