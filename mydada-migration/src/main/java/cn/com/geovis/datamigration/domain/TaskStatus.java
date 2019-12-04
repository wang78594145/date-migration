package cn.com.geovis.datamigration.domain;


/**
 * @author wangqianyi
 * @Title: TaskStatus
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/20 13:35
 */
public enum TaskStatus {

    NEW("新建"), RUNNING("正在运行"),
    SUCCESS("运行成功"), FAILED("运行失败"),
    PAUSE("暂停运行"),CONTINUE("继续运行");


    private String name;

    TaskStatus(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
