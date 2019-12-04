package cn.com.geovis.datamigration.service.impl;

import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.mapper.DataSourceMapper;
import cn.com.geovis.datamigration.mapper.DatasourceItemMapper;
import cn.com.geovis.datamigration.service.DataSourceService;
import cn.com.geovis.datamigration.vo.req.DataSourceReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
public class DataSourceServiceImpl implements DataSourceService {

    private final DataSourceMapper dataSourceMapper;
    private final DatasourceItemMapper datasourceItemMapper;


    @Autowired
    public DataSourceServiceImpl(DataSourceMapper dataSourceMapper,
                                 DatasourceItemMapper datasourceItemMapper) {
        this.dataSourceMapper = dataSourceMapper;
        this.datasourceItemMapper = datasourceItemMapper;
    }


    @Override
    @Transactional
    public Response insertDataSource(DataSource dataSource) {
        try {
            dataSourceMapper.addDataSource(dataSource);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "添加数据源失败");
        }
    }


    public Response updateDataSourceById(DataSource item) {
        dataSourceMapper.updateDataSourceById(item);
        return Response.success();
    }

    @Override
    @Transactional
    public Response deleteDataSourceById(Integer id) {
        try {
            dataSourceMapper.deleteDataSourceById(id);
            datasourceItemMapper.deleteItemByDataSourceId(id);
            return Response.success();
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "删除数据源失败");
        }
    }


    @Override
    @Transactional
    public Response getDataSources(DataSourceReq dataSourceReq) {
        int page = dataSourceReq.getPage();
        int pageSize = dataSourceReq.getPageSize();

        try{
            PageHelper.startPage(page,pageSize);//改写语句实现分页查询
            List<DataSource> dataSources = dataSourceMapper.getDataSource(dataSourceReq);
            PageInfo<DataSource> info=new PageInfo<>(dataSources);

            return Response.success(info);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "获取数据源列表失败");
        }
    }

    @Override
    @Transactional
    public Response getDataSourceById(Integer dsId) {
        try {
            DataSource dataSource = dataSourceMapper.getdateSourceById(dsId);
            return Response.success(dataSource);
        } catch (Exception e) {
            e.printStackTrace();
            return Response.error(500, "获取数据源失败");
        }
    }

}
