package com.goffy.seckill.controller;

import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.seckill.pojo.SeckillVouchers;
import com.goffy.seckill.pojo.SeckillVouchersDTO;
import com.goffy.seckill.service.SeckillService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.text.ParseException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/14/14:57
 * @Description: 秒杀控制层
 */
@RestController
public class SeckillController {

    @Resource
    private SeckillService seckillService;

    @Resource
    private HttpServletRequest request;

    /**
     * 新增秒杀活动
     *
     * @param seckillVouchers
     * @return
     */
    @PostMapping("/add")
    public ResultInfo<String> addSeckillVouchers(@RequestBody SeckillVouchersDTO seckillVouchers){
        seckillService.addSeckillVouchers(seckillVouchers);
        return ResultInfoUtil.buildSuccess(request.getServletPath(),
                "添加成功");
    }

    /**
     * 秒杀下单
     *
     * @param voucherId
     * @param access_token
     * @return
     */
    @PostMapping("{voucherId}")
    public ResultInfo<String> doSeckill(@PathVariable Integer voucherId, String access_token) {
        ResultInfo resultInfo = seckillService.doSeckill(voucherId, access_token, request.getServletPath());
        return resultInfo;
    }
}
