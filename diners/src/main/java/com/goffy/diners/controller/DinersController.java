package com.goffy.diners.controller;

import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.model.dto.DinersDTO;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.diners.service.DinersService;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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


    /**
     * 注册
     *
     * @param dinersDTO
     * @return
     */
    @PostMapping("/register")
    public ResultInfo register(@RequestBody DinersDTO dinersDTO) {
        return dinersService.register(dinersDTO, request.getServletPath());
    }

    /**
     * 校验手机号是否已注册
     *
     * @param phone
     * @return
     */
    @GetMapping("/checkPhone")
    public ResultInfo checkPhone(String phone) {
        dinersService.checkPhoneIsRegistered(phone);
        return ResultInfoUtil.buildSuccess(request.getServletPath());
    }

    /**
     * 登录
     *
     * @param account
     * @param password
     * @return
     */
    @GetMapping("/signin")
    public ResultInfo signIn(String account, String password){
        return dinersService.signIn(account, password, request.getServletPath());
    }

}
