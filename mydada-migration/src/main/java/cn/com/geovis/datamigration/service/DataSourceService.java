package cn.com.geovis.datamigration.service;

import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.vo.req.DataSourceReq;
import cn.com.geovis.datamigration.vo.resp.Response;


public interface DataSourceService {
    Response insertDataSource(DataSource dataSource);

    Response deleteDataSourceById(Integer ds_id);


    Response getDataSources(DataSourceReq dataSourceReq);

    Response getDataSourceById(Integer ds_id);

    Response updateDataSourceById(DataSource dataSource);
}
