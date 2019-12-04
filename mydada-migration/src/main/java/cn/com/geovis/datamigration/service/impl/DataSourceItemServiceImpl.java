package cn.com.geovis.datamigration.service.impl;


import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.domain.DatasourceItem;
import cn.com.geovis.datamigration.mapper.DataSourceMapper;
import cn.com.geovis.datamigration.mapper.DatasourceItemMapper;
import cn.com.geovis.datamigration.service.IDataSourceItemService;
import cn.com.geovis.datamigration.vo.req.TableInfoReq;
import cn.com.geovis.datamigration.vo.req.TableInfoUpdateReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @author wangqianyi
 * @Title: DataSourceItemServiceImpl
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/22 14:30
 */

@Service
public class DataSourceItemServiceImpl implements IDataSourceItemService {

    private DatasourceItemMapper datasourceItemMapper;
    private DataSourceMapper dataSourceMapper;

    @Autowired
    public DataSourceItemServiceImpl(DatasourceItemMapper datasourceItemMapper,DataSourceMapper dataSourceMapper) {
        this.datasourceItemMapper = datasourceItemMapper;
        this.dataSourceMapper = dataSourceMapper;
    }

    @Override
    public Response updateDataSourceItemById(TableInfoUpdateReq item) {
        datasourceItemMapper.updateItemById(item);
        return Response.success();
    }

    @Override
    public Response addDataSourceItem(DatasourceItem item) throws Exception {
        int dsId=item.getDsId();
        DataSource dataSource = dataSourceMapper.getdateSourceById(dsId);
        boolean flag = dataSource.validator(item);
        if(flag){
            datasourceItemMapper.addDatasourceItem(item);
            return Response.success();
        }else {
            throw new Exception("数据源表无效");
        }

    }

    @Override
    public Response deleteDataSourceItemById(int id) {
        datasourceItemMapper.deleteItemById(id);
        return Response.success();
    }

    @Override

    public Response getDataSourceItem(TableInfoReq itemReq) throws DataAccessException{
            PageHelper.startPage(itemReq.getPage(),itemReq.getPageSize());//改写语句实现分页查询
            List<DatasourceItem> items =  datasourceItemMapper.getDataSourceItem(itemReq);
            PageInfo<DatasourceItem> info=new PageInfo<>(items);
            return Response.success(info);

    }
}
