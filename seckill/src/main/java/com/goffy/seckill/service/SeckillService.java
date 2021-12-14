package com.goffy.seckill.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.goffy.commons.constant.ApiConstant;
import com.goffy.commons.constant.RedisKeyConstant;
import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.model.vo.SignInDinerInfo;
import com.goffy.commons.utils.AssertUtil;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.seckill.mapper.SeckillVouchersMapper;
import com.goffy.seckill.mapper.VoucherOrderMapper;
import com.goffy.seckill.pojo.SeckillVouchers;
import com.goffy.seckill.pojo.SeckillVouchersDTO;
import com.goffy.seckill.pojo.VoucherOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/14/15:00
 * @Description:
 */
@Service
public class SeckillService{

    @Resource
    private RedisTemplate redisTemplate;

    @Resource
    private SeckillVouchersMapper seckillVouchersMapper;

    @Value("${service.name.ms-oauth-server}")
    private String oauthServerName;

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private VoucherOrderMapper voucherOrderMapper;

    @Transactional(rollbackFor = Exception.class)
    public void addSeckillVouchers(SeckillVouchersDTO seckillVouchers) {


        // 非空校验
        AssertUtil.isTrue(seckillVouchers.getFkVoucherId() == null, "请选择需要抢购的代金券");
        AssertUtil.isTrue(seckillVouchers.getAmount() == 0, "请输入抢购总数量");
        Date now = new Date();
        AssertUtil.isNotNull(seckillVouchers.getStartTime(), "请输入开始时间");
        // 生产环境下面一行代码需放行，这里注释方便测试
        // AssertUtil.isTrue(now.after(seckillVouchers.getStartTime()), "开始时间不能早于当前时间");
        AssertUtil.isNotNull(seckillVouchers.getEndTime(), "请输入结束时间");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "结束时间不能早于当前时间");
        AssertUtil.isTrue(seckillVouchers.getStartTime().after(seckillVouchers.getEndTime()), "开始时间不能晚于结束时间");


        // 验证数据库中是否已经存在该券的秒杀活动
        // 高并发场景下这里会存在问题，因为判断和查询是两个操作，改用Redis实现
        /*SeckillVouchers seckillVouchersFromDb = seckillVouchersMapper.selectVoucher(seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(seckillVouchersFromDb != null, "该券已经拥有了抢购活动");
        //seckillVouchersMapper.insert(seckillVouchers);
        seckillVouchersMapper.save(seckillVouchers);*/

        // 采用 Redis 实现
        String key = RedisKeyConstant.seckill_vouchers.getKey()+ seckillVouchers.getFkVoucherId();
        Map<String, Object> entries = redisTemplate.opsForHash().entries(key);
        AssertUtil.isTrue(!entries.isEmpty()&&(int)entries.get("amount")>0,"该券已经拥有了抢购活动");
        // 插入 Redis
        seckillVouchers.setIsValid(1);
        seckillVouchers.setCreateDate(now);
        seckillVouchers.setUpdateDate(now);
        redisTemplate.opsForHash().putAll(key, BeanUtil.beanToMap(seckillVouchers));
    }

    /**
     * 抢购代金劵
     * @param voucherId
     * @param accessToken
     * @param servletPath
     * @return
     */
    public ResultInfo doSeckill(Integer voucherId, String accessToken, String servletPath) {
        // 基本参数校验
        AssertUtil.isTrue(voucherId == null || voucherId < 0, "请选择需要抢购的代金券");
        AssertUtil.isNotEmpty(accessToken, "请登录");

        // 判断此代金券是否加入抢购
        /* SeckillVouchers seckillVouchers = seckillVouchersMapper.selectVoucher(voucherId);
         AssertUtil.isTrue(seckillVouchers == null, "该代金券并未有抢购活动");
         //判断是否有效
         AssertUtil.isTrue(seckillVouchers.getIsValid() == 0, "该活动已结束");*/
        // 采用 Redis
        String key = RedisKeyConstant.seckill_vouchers.getKey() + voucherId;
        Map<String, Object> map = redisTemplate.opsForHash().entries(key);
        SeckillVouchers seckillVouchers = BeanUtil.mapToBean(map, SeckillVouchers.class, true, null);


        // 判断是否开始、结束
        Date now = new Date();
        AssertUtil.isTrue(now.before(seckillVouchers.getStartTime()), "该抢购还未开始");
        AssertUtil.isTrue(now.after(seckillVouchers.getEndTime()), "该抢购已结束");
        // 判断是否卖完
        AssertUtil.isTrue(seckillVouchers.getAmount() < 1, "该券已经卖完了");
        // 获取登录用户信息
       /* String url = oauthServerName + "user/me?access_token={accessToken}";
        ResultInfo resultInfo = restTemplate.getForObject(url, ResultInfo.class, accessToken);
        if (resultInfo.getCode() != ApiConstant.SUCCESS_CODE) {
            resultInfo.setPath(servletPath);
            return resultInfo;
        }*/
        // 这里的data是一个LinkedHashMap，SignInDinerInfo
        /*SignInDinerInfo dinerInfo = BeanUtil.fillBeanWithMap((LinkedHashMap) resultInfo.getData(),
                new SignInDinerInfo(), false);
*/
        // 判断登录用户是否已抢到(一个用户针对这次活动只能买一次)
        /*VoucherOrder order = voucherOrderMapper.findDinerOrder(1,
                seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(order != null, "该用户已抢到该代金券，无需再抢");*/

        // 扣库存
        /*int count = seckillVouchersMapper.stockDecrease(seckillVouchers.getId());
        AssertUtil.isTrue(count == 0, "该券已经卖完了");*/


        VoucherOrder voucherOrders = new VoucherOrder();
        voucherOrders.setFkDinerId(1);
        // Redis 中不需要维护外键信息
        voucherOrders.setFkSeckillId(seckillVouchers.getId());
        voucherOrders.setFkVoucherId(seckillVouchers.getFkVoucherId());
        String orderNo = IdUtil.getSnowflake(1, 1).nextIdStr();
        voucherOrders.setOrderNo(orderNo);
        voucherOrders.setOrderType(1);
        voucherOrders.setStatus((byte)0);
        long i = voucherOrderMapper.save(voucherOrders);
        AssertUtil.isTrue(i == 0, "用户抢购失败");
        return ResultInfoUtil.buildSuccess(servletPath, "抢购成功");
    }
}
