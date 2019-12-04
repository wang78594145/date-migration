package cn.com.geovis.datamigration.mapper;

import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.vo.req.DataSourceReq;
import org.apache.ibatis.annotations.*;
import org.apache.ibatis.mapping.FetchType;

import java.util.List;

public interface DataSourceMapper {



    @Insert("insert into datasource (id,type,name,address) values (nextval('datasource_seq'),#{type},#{name},#{address})")
    @Options(useGeneratedKeys = true, keyColumn = "id")
    int addDataSource(DataSource dataSource);

    @Delete("delete from datasource where id = #{id}")
    boolean deleteDataSourceById(Integer id);

    @Select("select * from datasource where id=#{id}")
    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="type",property="type"),
            @Result(column="name",property="name"),
            @Result(column="address",property="address"),
            @Result(column="id",property="items",
                    many=@Many(
                            select="cn.com.geovis.datamigration.mapper.DatasourceItemMapper.getDataSourceItemByDataSourceId",
                            fetchType= FetchType.EAGER
                    )
            )
    })
    DataSource getdateSourceById(@Param("id") Integer id);

    @Results({
            @Result(id=true,column="id",property="id"),
            @Result(column="type",property="type"),
            @Result(column="name",property="name"),
            @Result(column="address",property="address"),
            @Result(column="id",property="items",
                    many=@Many(
                            select="cn.com.geovis.datamigration.mapper.DatasourceItemMapper.getDataSourceItemByDataSourceId",
                            fetchType= FetchType.EAGER
                    )
            )
    })
    @Select({"<script>",
            "select * from datasource ",
            " where 1=1",
            "<when test='type!=0'>",
            "and type = #{type} ",
            "</when>",
            "order by create_time desc",
            "</script>"})
    List<DataSource> getDataSource(DataSourceReq dataSourceReq);

    @Update({"<script>",
            "update datasource ",
            "set type = type ",
            "<when test='name!=null'>",
            "name = #{name},",
            "</when>",
            "<when test='address!=null'>",
            "address = #{address}",
            "</when>",
            "</script>"})
    boolean updateDataSourceById(DataSource dataSource);

}
