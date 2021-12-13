package com.goffy.diners.controller;

import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.diners.service.DinersService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/13/13:57
 * @Description: 食客服务控制层
 */

@RestController
@Api(tags = "食客相关接口")
public class DinersController {

    @Resource
    private DinersService dinersService;

    @Resource
    private HttpServletRequest request;


    @GetMapping("/signin")
    public ResultInfo signIn(String account, String password){
        return dinersService.signIn(account, password, request.getServletPath());
    }

}
