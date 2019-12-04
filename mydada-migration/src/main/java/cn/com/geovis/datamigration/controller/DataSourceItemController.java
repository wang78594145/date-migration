package cn.com.geovis.datamigration.controller;


import cn.com.geovis.datamigration.domain.DatasourceItem;
import cn.com.geovis.datamigration.service.IDataSourceItemService;
import cn.com.geovis.datamigration.vo.req.TableInfoReq;
import cn.com.geovis.datamigration.vo.req.TableInfoUpdateReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @author wangqianyi
 * @Title: DataSourceItemController
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/22 13:23
 */


@Api(tags = "表信息管理", description = "表信息增删改查接口")
@RestController
public class DataSourceItemController extends BaseController {

    private IDataSourceItemService dataSourceItemService;

    @Autowired
    public DataSourceItemController(IDataSourceItemService dataSourceItemService) {
        this.dataSourceItemService = dataSourceItemService;
    }

    //根据ds_id获取对应的表名称列表
    @ApiOperation(value = "获取表信息", notes = "获取表信息")
    @RequestMapping(value = "/tables", method = RequestMethod.GET)
    public Response getTables(TableInfoReq tableInfoReq) {
        return dataSourceItemService.getDataSourceItem(tableInfoReq);
    }

    //修改数据源表信息
    @ApiOperation(value = "修改表信息", notes = "修改指定数据源下表信息")
    @RequestMapping(value = "/table/{id}", method = RequestMethod.PUT)
    public Response updateTableInfo(@RequestBody TableInfoUpdateReq item) {
        return dataSourceItemService.updateDataSourceItemById(item);
    }

    //删除名称
    @ApiOperation(value = "删除表信息")
    @ApiImplicitParam(name = "id", value = "表id主键", required = true)
    @RequestMapping(value = "/table/{id}", method = RequestMethod.DELETE)
    public Response deleteDataSourceItem(@PathVariable Integer id) {
        return dataSourceItemService.deleteDataSourceItemById(id);
    }

    //添加新表
    @ApiOperation(value = "添加一条表数据")

    @RequestMapping(value = "/table/new", method = RequestMethod.PUT)
    public Response addDataSourceItem(@RequestBody @Validated DatasourceItem item, BindingResult result) throws Exception {
        if (result.hasErrors()) {
            return Response.error(1, generateErrorMessage(result));
        }
        try {
            return dataSourceItemService.addDataSourceItem(item);
        } catch (DataAccessException e) {
            if (e.getRootCause().getMessage().contains("重复键违反唯一约束"))
                return Response.error(500, "当前数据源下已存在表" + item.getName() + "请不要重复添加");
            else
                throw e;
        }

    }
}
