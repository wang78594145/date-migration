package cn.com.geovis.datamigration.vo.resp;


import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.domain.Task;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * @author wangqianyi
 * @Title: TaskDetailsResp
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/20 14:22
 */

@Data
@Accessors(chain = true)
public class TaskDetailsResp {
    private DataSource dataSource;
    private Task task;
}
