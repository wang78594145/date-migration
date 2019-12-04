package cn.com.geovis.datamigration.vo.resp;


import lombok.Data;
import lombok.experimental.Accessors;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wangqianyi
 * @Title: BaseResponse
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/18 11:12
 */

@Slf4j
@Data
@Accessors(chain = true)
public class Response {
    private int code;
    private String message;
    private Object data;


    //当返回正常返参时
    public static Response success(Object data){
        Response response = new Response();
        response.setCode(200).setMessage("ok").setData(data);
        return  response;
    }

    //请求无具体返参时
    public static Response success(){
        return success(null);
    }

    //请求发生异常时
    public static Response error(int code,String message){
        Response response = new Response();
        response.setCode(code).setMessage(message);
        return response;
    }

}
