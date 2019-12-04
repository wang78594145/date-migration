package cn.com.geovis.datamigration.vo.req;


import lombok.Data;

/**
 * @author wangqianyi
 * @Title: DataSourceReq
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/27 9:45
 */

@Data
public class DataSourceReq {

    private int page;
    private int pageSize;
    private int type = 0;

    public void setType(String type) {
        if (type.equals("")) {
            this.type = 0;
        }else {
            this.type = Integer.parseInt(type);
        }
    }
}
