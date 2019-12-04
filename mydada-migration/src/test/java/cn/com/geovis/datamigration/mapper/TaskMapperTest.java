package cn.com.geovis.datamigration.mapper;

import cn.com.geovis.datamigration.DataMigrationApplication;
import cn.com.geovis.datamigration.domain.Task;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import static org.junit.Assert.*;


/**
 * @author wangqianyi
 * @Title: TaskMapperTest
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/25 9:46
 */

@RunWith(SpringRunner.class)
@SpringBootTest(classes={DataMigrationApplication.class})// 指定启动类
public class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void getTaskById() {
        System.out.println(taskMapper.getTaskById(2));
    }
}