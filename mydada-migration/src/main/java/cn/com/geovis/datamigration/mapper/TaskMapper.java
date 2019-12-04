package cn.com.geovis.datamigration.mapper;


import cn.com.geovis.datamigration.domain.Task;
import cn.com.geovis.datamigration.vo.req.TaskReq;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

/**
 * @author wangqianyi
 * @Title: TaskMapper
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/15 16:46
 */


public interface TaskMapper {


    @Insert("INSERT INTO tasks(name,source_type,source_address,source_tablename," +
            "lat_begin,lat_end,lon_begin,lon_end,output_format,output_path," +
            "layer_min,layer_max,status,comments,pre_code,epsg,rowkey_type) VALUES " +
            "(#{name},#{sourceType},#{sourceAddress},#{sourceTablename}, " +
            "#{latBegin},#{latEnd},#{lonBegin},#{lonEnd},#{outputFormat}," +
            "#{outputPath},#{layerMin},#{layerMax},#{status},#{comments},#{preCode},#{epsg},#{rowkeyType});")
    boolean createTask(Task task);


    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "source_type", property = "sourceType"),
            @Result(column = "source_address", property = "sourceAddress"),
            @Result(column = "source_tablename", property = "sourceTablename"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "finish_time", property = "finishTime"),
            @Result(column = "lat_begin", property = "latBegin"),
            @Result(column = "lat_end", property = "latEnd"),
            @Result(column = "lon_begin", property = "lonBegin"),
            @Result(column = "lon_end", property = "lonEnd"),
            @Result(column = "input_format", property = "inputFormat"),
            @Result(column = "output_format", property = "outputFormat"),
            @Result(column = "output_path", property = "outputPath"),
            @Result(column = "task_total", property = "taskTotal"),
            @Result(column = "task_done", property = "taskDone"),
            @Result(column = "progress", property = "progress"),
            @Result(column = "layer_min", property = "layerMin"),
            @Result(column = "layer_max", property = "layerMax"),
            @Result(column = "status", property = "status"),
            @Result(column = "comments", property = "comments"),
            @Result(column = "pre_code", property = "preCode"),
            @Result(column = "epsg", property = "epsg"),
            @Result(column = "rowkey_type", property = "rowkeyType")

    })
    @Select("select * from tasks where id = #{id}")
    Task getTaskById(int id);

    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "source_type", property = "sourceType"),
            @Result(column = "source_address", property = "sourceAddress"),
            @Result(column = "source_tablename", property = "sourceTablename"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "finish_time", property = "finishTime"),
            @Result(column = "lat_begin", property = "latBegin"),
            @Result(column = "lat_end", property = "latEnd"),
            @Result(column = "lon_begin", property = "lonBegin"),
            @Result(column = "lon_end", property = "lonEnd"),
            @Result(column = "input_format", property = "inputFormat"),
            @Result(column = "output_format", property = "outputFormat"),
            @Result(column = "output_path", property = "outputPath"),
            @Result(column = "task_total", property = "taskTotal"),
            @Result(column = "task_done", property = "taskDone"),
            @Result(column = "progress", property = "progress"),
            @Result(column = "layer_min", property = "layerMin"),
            @Result(column = "layer_max", property = "layerMax"),
            @Result(column = "status", property = "status"),
            @Result(column = "comments", property = "comments"),
            @Result(column = "pre_code", property = "preCode"),
            @Result(column = "epsg", property = "epsg"),
            @Result(column = "rowkey_type", property = "rowkeyType")
    })
    @Select({"<script>",
            "SELECT * FROM tasks",
            "WHERE 1=1",
            "<when test='status!=null'>",
            "AND status = #{status}",
            "</when>",
            "<when test='name!=null'>",
            "AND name like CONCAT('%',#{name},'%') ",
            "</when>",
            "order by create_time desc",
            "</script>"})
    List<Task> getAllTask(TaskReq taskReq);


    @Results({
            @Result(column = "id", property = "id", id = true),
            @Result(column = "name", property = "name"),
            @Result(column = "source_type", property = "sourceType"),
            @Result(column = "source_address", property = "sourceAddress"),
            @Result(column = "source_tablename", property = "sourceTablename"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "finish_time", property = "finishTime"),
            @Result(column = "lat_begin", property = "latBegin"),
            @Result(column = "lat_end", property = "latEnd"),
            @Result(column = "lon_begin", property = "lonBegin"),
            @Result(column = "lon_end", property = "lonEnd"),
            @Result(column = "input_format", property = "inputFormat"),
            @Result(column = "output_format", property = "outputFormat"),
            @Result(column = "output_path", property = "outputPath"),
            @Result(column = "task_total", property = "taskTotal"),
            @Result(column = "task_done", property = "taskDone"),
            @Result(column = "progress", property = "progress"),
            @Result(column = "layer_min", property = "layerMin"),
            @Result(column = "layer_max", property = "layerMax"),
            @Result(column = "status", property = "status"),
            @Result(column = "comments", property = "comments"),
            @Result(column = "pre_code", property = "preCode"),
            @Result(column = "epsg", property = "epsg"),
            @Result(column = "rowkey_type", property = "rowkeyType")
    })
    @Select("select * from tasks where status = '新建' " +
            "or status = '继续运行' order by id limit #{limitNum};")
    List<Task> getNewTask(int limitNum);

    @Update("update tasks set status=#{status} where id =#{id};")
    boolean updateStatus(String status, int id);

    @Update("update tasks set task_total=#{taskTotal},task_done=#{taskDone},progress=#{progress} where id =#{id};")
    boolean updateProgress(int taskTotal, int taskDone, short progress, int id);

    @Update("update tasks set comments=#{comments} where id =#{id};")
    boolean updateCommet(String comments, int id);

    @Results({
            @Result(column = "task_id", property = "taskId", id = true),
            @Result(column = "task_name", property = "taskName"),
            @Result(column = "create_time", property = "createTime"),
            @Result(column = "finish_time", property = "finishTime"),
            @Result(column = "dsource_id", property = "dsourceId"),
            @Result(column = "lat_begin", property = "latBegin"),
            @Result(column = "lat_end", property = "latEnd"),
            @Result(column = "lon_begin", property = "lonBegin"),
            @Result(column = "lon_end", property = "lonEnd"),
            @Result(column = "output_format", property = "outputFormat"),
            @Result(column = "output_path", property = "outputPath"),
            @Result(column = "task_total", property = "taskTotal"),
            @Result(column = "task_done", property = "taskDone"),
            @Result(column = "progress", property = "progress"),
            @Result(column = "layer_min", property = "layerMin"),
            @Result(column = "layer_max", property = "layerMax"),
            @Result(column = "task_status", property = "taskStatus"),
            @Result(column = "comments", property = "comments"),
            @Result(column = "pre_code", property = "preCode"),
            @Result(column = "espg", property = "espg"),
            @Result(column = "rowkey_type", property = "rowkeyType")
    })

    @Select("select * from tasks where source_type = 2 and status = #{taskStatus} order by id limit 1" +
            "")
    List<Task> getTaskByStatus(String taskStatus);

    @Select("select count(1) from tasks where source_type = 2 and status = #{taskStatus} " +
            "")
    int getTaskCountByStatus(String taskStatus);

    @Update("UPDATE tasks SET task_total=#{taskTotal} WHERE id = #{id}")
    boolean updateTaskTotalById(@Param("id") int id, @Param("taskTotal") Integer taskTotal);

    @Update("UPDATE tasks SET task_done=#{taskDone} WHERE id = #{id}")
    boolean updateTaskDoneById(@Param("id") int id, @Param("taskDone") int taskDoneNum);

    @Update("UPDATE tasks SET status = #{status} WHERE id = #{id}")
    boolean updateStatusById(@Param("id") int id, @Param("status") String status);

    @Update("UPDATE tasks SET finish_time=#{finishTime} WHERE id = #{id}")
    boolean updateFinishTimeById(@Param("id") int id, @Param("finishTime") LocalDateTime finishTime);

    @Update("UPDATE tasks SET    progress=#{progress} WHERE id = #{id}")
    boolean updateProgressById(@Param("id") int id, @Param("progress") short progress);

    @Select("select count(*) from tasks where status ='正在运行';")
    int getRunningTasksNum();

}
