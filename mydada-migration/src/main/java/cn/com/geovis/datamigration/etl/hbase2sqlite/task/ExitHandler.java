package cn.com.geovis.datamigration.etl.hbase2sqlite.task;



import cn.com.geovis.datamigration.etl.hbase2sqlite.hbase.ExportByLevel;

/**
 * @author wangqianyi
 * @Title: ExitHandler
 * @ProjectName hbase-scan-export
 * @Description: TODO
 * @date 2018/12/19 15:34
 */
public class ExitHandler extends Thread {
    private ExportByLevel byLevel;

    public ExitHandler(ExportByLevel byLevel) {
        this.byLevel = byLevel;
    }


    @Override
    public void run() {
//        byLevel.logException("log success");
    }
}
