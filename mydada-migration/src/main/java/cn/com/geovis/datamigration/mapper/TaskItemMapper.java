package cn.com.geovis.datamigration.mapper;


import cn.com.geovis.datamigration.domain.TaskItem;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface TaskItemMapper {

    @Insert("INSERT INTO task_item(task_id,name,status) VALUES " +
            "(#{taskId},#{name},#{status});")
    boolean insertTaskItem(TaskItem taskItem);

    @Select("select count(1) from task_item where task_id = #{taskId} and task_item.status ='运行成功'")
    int getTaskItemCountById(int taskId);

    @Select("select name from task_item where task_id = #{taskId} and task_item.status ='运行成功'")
    List<String> getSuccessNameById(int taskId);


    @Insert("INSERT INTO task_item(task_id,name,status) VALUES (#{taskId},#{name},#{status});")
    boolean createItem(int taskId,String name,String status);


}
