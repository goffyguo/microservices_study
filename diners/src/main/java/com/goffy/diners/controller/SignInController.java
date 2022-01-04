package com.goffy.diners.controller;

import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.diners.service.SignInService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;

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

    /**
     * Bitmaps 叫位图，他不是Redis的基本数据类型，而是基于String数据类型的按位操作，高阶数据类型的一种。
     * Bitmaps 最大支持数为2^32位，使用512M内存就可以存储多大42.9亿的字节信息（2^32=4294967196）。
     * <p>
     * 由一组bit组成，每个bit对应0和1，虽然内部还是采用String类型存储，
     * 但是Redis提供了一些指令用于直接操作位图，可以把它看作是一个bit数组，数据的下标就是偏移量。
     * 它的优点是内存开销小、效率高且操作简单，很适合用于签到这类场景。
     */

    @Resource
    private SignInService signInService;
    @Resource
    private HttpServletRequest request;

    /**
     * 用户签到
     * @param access_token
     * @param dateStr
     * @return
     */
    @PostMapping
    public ResultInfo signIn(String access_token,String dateStr){
        int count = signInService.doSign(access_token, dateStr);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), count);
    }

    /**
     * 获取用户签到情况（当月）
     * @param access_token
     * @param dateStr
     * @return
     */
    @GetMapping
    public ResultInfo getSignInInfo(String access_token,String dateStr){
        Map<String, Boolean> map = signInService.getSignInfo(access_token, dateStr);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), map);
    }

    /**
     * 获取签到次数 默认当月
     *
     * @param access_token
     * @param date
     * @return
     */
    @GetMapping("count")
    public ResultInfo getSignCount(String access_token, String date) {
        Long count = signInService.getSignCount(access_token, date);
        return ResultInfoUtil.buildSuccess(request.getServletPath(), count);
    }
}
