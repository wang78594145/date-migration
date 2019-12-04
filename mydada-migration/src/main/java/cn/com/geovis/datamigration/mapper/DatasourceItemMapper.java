package cn.com.geovis.datamigration.mapper;


import cn.com.geovis.datamigration.domain.DatasourceItem;
import cn.com.geovis.datamigration.vo.req.TableInfoReq;
import cn.com.geovis.datamigration.vo.req.TableInfoUpdateReq;
import org.apache.ibatis.annotations.*;
import org.springframework.dao.DataAccessException;

import java.util.List;

/**
 * @author wangqianyi
 * @Title: DatasourceItemMapper
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/19 19:30
 */

public interface DatasourceItemMapper {

    @Insert("insert into datasource_item (ds_id,name,comment) values(#{dsId},#{name},#{comment})")
    void addDatasourceItem(DatasourceItem item) throws DataAccessException;

    @Update({"<script>",
            "update datasource_item",
            "set id = id ",
            "<if test='name!=null'>",
            ", name = #{name} ",
            "</if>",
            "<if test='comment!=null'>",
            ", comment = #{comment}  ",
            "</if>",
            "where id = #{id}",
            "</script>"})
    void updateItemById(TableInfoUpdateReq item);

    @Delete("delete from datasource_item where id = #{d}")
    void deleteItemById(Integer id);

    @Delete("delete from datasource_item where ds_id = #{ds_id}")
    void deleteItemByDataSourceId(Integer ds_id);

    @Results({
            @Result(column = "ds_id", property = "dsId")
    })
    @Select({"<script>",
            "SELECT * FROM datasource_item",
            "WHERE 1=1",
            "<when test='dsId!=0'>",
            "AND ds_id=#{dsId}",
            "</when>",
            "<when test='name!=null'>",
            "AND name like CONCAT(#{name},'%') ",
            "</when>",
            "<when test='comment!=null'>",
            "AND comment like CONCAT(#{comment},'%') ",
            "</when>",
            "</script>"})
    List<DatasourceItem> getDataSourceItem(TableInfoReq tableInfoReq);


    @Results({
            @Result(column = "ds_id", property = "dsId")
    })
    @Select({ "SELECT * FROM datasource_item WHERE   ds_id=#{dsId}"})
    List<DatasourceItem> getDataSourceItemByDataSourceId(int ds_id);
}
