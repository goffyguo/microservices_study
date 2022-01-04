package com.goffy.diners.controller;

import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.diners.service.SignInService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/01/04/15:28
 * @Description: 签到控制类
 */
@RestController
@RequestMapping("/signIn")
public class SignInController {

    @Resource
    private SignInService signInService;
    @Resource
    private HttpServletRequest request;

    @PostMapping
    public ResultInfo signIn(String access_token,String dateStr){
        int count = signInService.doSign(access_token, dateStr);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), count);
    }
}
