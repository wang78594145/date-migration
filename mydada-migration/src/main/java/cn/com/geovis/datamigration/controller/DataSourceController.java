package cn.com.geovis.datamigration.controller;


import cn.com.geovis.datamigration.domain.DataSource;
import cn.com.geovis.datamigration.service.DataSourceService;
import cn.com.geovis.datamigration.vo.req.DataSourceReq;
import cn.com.geovis.datamigration.vo.resp.Response;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import springfox.documentation.annotations.ApiIgnore;

import javax.validation.constraints.Null;
import java.util.List;

@Api(tags = "数据源管理" ,description = "数据源增删改查接口")
@RestController
public class DataSourceController extends BaseController{

    private final DataSourceService dataSourceService;

    @Autowired
    public DataSourceController(DataSourceService dataSourceService) {
        this.dataSourceService = dataSourceService;
    }

    @ApiOperation(value = "添加新数据源",notes = "添加新的数据源及其属性")
    @RequestMapping(value = "/datasource/new",method = RequestMethod.POST)
    public Response addDataSource(@RequestBody @Validated DataSource dataSource, BindingResult result){
        if (result.hasErrors()){
            return Response.error(1,generateErrorMessage(result));
        }
        return dataSourceService.insertDataSource(dataSource);
    }

    //所有的数据源分页查询
    @ApiOperation(value = "返回所有数据列表",notes="返回两表联合数据")
    @RequestMapping(value = "/datasources",method = RequestMethod.GET)
    public Response allData(DataSourceReq dataSourceReq){
        return dataSourceService.getDataSources(dataSourceReq);
    }

    //某个特定的数据源
    @ApiOperation(value = "返回特定数据",notes = "根据ds_id返回特定数据")
    @ApiImplicitParam(name = "id",value = "数据源id标识",required = true,paramType = "path")
    @RequestMapping(value = "/datasource/{id}",method = RequestMethod.GET)
    public Response getDataById(@PathVariable Integer id){
        return  dataSourceService.getDataSourceById(id);
    }


    //删除特定数据
    @ApiOperation(value = "删除数据",notes = "根据id删除特定数据")
    @ApiImplicitParam(name = "id",value = "数据源id标识",required = true)
    @RequestMapping(value = "/datasource/{id}",method = RequestMethod.DELETE)
    public Response deleteDataSource(@PathVariable Integer id){
        return dataSourceService.deleteDataSourceById(id);
    }

    //修改表名称
    @ApiIgnore
    @ApiImplicitParam(name = "id",value = "数据源id标识",required = true)
    @RequestMapping(value = "/datasource/update",method = RequestMethod.PUT)
    public Response updateDataSource(@RequestBody DataSource dataSource){
        return  dataSourceService.updateDataSourceById(dataSource);
    }
}
