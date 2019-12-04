package cn.com.geovis.datamigration.etl.hbase2sqlite.sqlite;

public class Pair {
    private int col;
    private int row;

    public Pair( int col, int row) {
        this.col = col;
        this.row = row;

    }

    public int getCol() {
        return col;
    }

    public int getRow() {
        return row;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
