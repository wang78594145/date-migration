package cn.com.geovis.datamigration.service;


import cn.com.geovis.datamigration.domain.DatasourceItem;
import cn.com.geovis.datamigration.vo.req.TableInfoReq;
import cn.com.geovis.datamigration.vo.req.TableInfoUpdateReq;
import cn.com.geovis.datamigration.vo.resp.Response;

/**
 * @author wangqianyi
 * @Title: IDataSourceItemService
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/22 13:22
 */
public interface IDataSourceItemService {
    Response updateDataSourceItemById(TableInfoUpdateReq item);

    Response addDataSourceItem(DatasourceItem item) throws Exception;

    Response deleteDataSourceItemById(int id);

    Response getDataSourceItem(TableInfoReq itemReq);
}


