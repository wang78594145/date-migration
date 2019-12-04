package cn.com.geovis.datamigration.controller;


import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

import java.util.List;

/**
 * @author wangqianyi
 * @Title: BaseController
 * @ProjectName data-migration
 * @Description: TODO
 * @date 2019/3/26 14:59
 */
public class BaseController {
    protected String generateErrorMessage(BindingResult result){
        List<ObjectError> list = result.getAllErrors();
        StringBuilder sb = new StringBuilder();
        for (ObjectError error :list) {
            sb.append(error.getDefaultMessage()).append("\n");
        }
        return sb.toString();
    }
}
