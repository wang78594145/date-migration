package cn.com.geovis.datamigration.etl.hbase2sqlite.sqlite;

public class LCRData {
    private int col;
    private int row;
    private int level;
    private byte[] images;

    public LCRData(int[] lcr, byte[] images) {
        this.level = lcr[0];
        this.col = lcr[1];
        this.row = lcr[2];
        this.images = images;
    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    public int getLevel() {
        return level;
    }

    public byte[] getImages() {
        return images;
    }
}
