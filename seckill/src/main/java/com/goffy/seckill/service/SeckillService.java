package com.goffy.seckill.service;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.goffy.commons.constant.ApiConstant;
import com.goffy.commons.constant.RedisKeyConstant;
import com.goffy.commons.exception.ParameterException;
import com.goffy.commons.model.domain.ResultInfo;
import com.goffy.commons.model.vo.SignInDinerInfo;
import com.goffy.commons.utils.AssertUtil;
import com.goffy.commons.utils.ResultInfoUtil;
import com.goffy.seckill.mapper.SeckillVouchersMapper;
import com.goffy.seckill.mapper.VoucherOrderMapper;
import com.goffy.seckill.model.RedisLock;
import com.goffy.seckill.pojo.SeckillVouchers;
import com.goffy.seckill.pojo.SeckillVouchersDTO;
import com.goffy.seckill.pojo.VoucherOrder;
import jodd.time.TimeUtil;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/14/15:00
 * @Description:
 */
@Service
public class SeckillService {

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

    @Resource
    private DefaultRedisScript defaultRedisScript;

    @Resource
    private RedisLock redisLock;

    @Resource
    private RedissonClient redissonClient;

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
        String key = RedisKeyConstant.seckill_vouchers.getKey() + seckillVouchers.getFkVoucherId();
        Map<String, Object> entries = redisTemplate.opsForHash().entries(key);
        AssertUtil.isTrue(!entries.isEmpty() && (int) entries.get("amount") > 0, "该券已经拥有了抢购活动");
        // 插入 Redis
        seckillVouchers.setIsValid(1);
        seckillVouchers.setCreateDate(now);
        seckillVouchers.setUpdateDate(now);
        redisTemplate.opsForHash().putAll(key, BeanUtil.beanToMap(seckillVouchers));
    }

    /**
     * 抢购代金劵
     *
     * @param voucherId
     * @param accessToken
     * @param servletPath
     * @return
     */
    @Transactional(rollbackFor = Exception.class)
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
        VoucherOrder order = voucherOrderMapper.findDinerOrder(2,
                seckillVouchers.getFkVoucherId());
        AssertUtil.isTrue(order != null, "该用户已抢到该代金券，无需再抢");

        // 扣库存
        /*int count = seckillVouchersMapper.stockDecrease(seckillVouchers.getId());
        AssertUtil.isTrue(count == 0, "该券已经卖完了");*/

        String lockName = RedisKeyConstant.lock_key.getKey()+ "2" + ":" + voucherId;
        long expireTime = seckillVouchers.getEndTime().getTime() - now.getTime();
        // 自定义Redis分布式锁
        //String lockKey = redisLock.tryLock(lockName, expireTime);
        //使用redisson分布式锁
        RLock lock = redissonClient.getLock(lockName);

        try {
            //if(StrUtil.isNotBlank(lockKey)){
            boolean isLocked = lock.tryLock(expireTime, TimeUnit.MILLISECONDS);
            if(isLocked){
                // 下单
                VoucherOrder voucherOrders = new VoucherOrder();
                voucherOrders.setFkDinerId(2);
                // Redis 中不需要维护外键信息
                //voucherOrders.setFkSeckillId(seckillVouchers.getId());
                voucherOrders.setFkVoucherId(seckillVouchers.getFkVoucherId());
                String orderNo = IdUtil.getSnowflake(1, 1).nextIdStr();
                voucherOrders.setOrderNo(orderNo);
                voucherOrders.setOrderType(1);
                voucherOrders.setStatus((byte) 0);
                long i = voucherOrderMapper.save(voucherOrders);
                AssertUtil.isTrue(i == 0, "用户抢购失败");
                // 从Redis里面扣库存
                /*long amount = redisTemplate.opsForHash().increment(key, "amount", -1);
                AssertUtil.isTrue(amount<0, "该券已经卖完了");*/
                // 采用 Redis+lua 解决问题
                // 扣库存
                List<String> keys = new ArrayList<>();
                keys.add(key);
                keys.add("amount");
                Long amount = (Long) redisTemplate.execute(defaultRedisScript, keys);
                AssertUtil.isTrue(amount == null || amount < 1, "该券已经卖完了");
            }
        } catch (Exception e) {
            // 手动回滚事务
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            // 自定义 Redis 解锁
            //redisLock.unlock(lockName, lockKey);
            //使用redisson分布式锁解锁
            lock.unlock();
            if (e instanceof ParameterException) {
                return ResultInfoUtil.buildError(0, "该券已经卖完了", servletPath);
            }
        }
        return ResultInfoUtil.buildSuccess(servletPath, "抢购成功");
    }


    /*
    * lock
    * local key = KEYS[1]; -- 第一个参数 锁的KEY
    local threadId = ARGV[1]; -- 第二个参数 线程唯一标识
    local releaseTime = ARGV[2]; -- 第三个参数 锁的自动释放时间

    if(redis.call('exists', key) == 0) then -- 判断锁是否已经存在
        redis.call('hset', key, threadId, '1'); -- 不存在 则获取锁
        redis.call('expire', key, releaseTime); -- 设置有效期
        return 1; -- 返回结果
    end;

    if(redis.call('hexists', key, threadId) == 1) then -- 锁已经存在，判断threadId是都是自己
        redis.call('hincrby', key, threadId, '1'); --如果是自己 则重入次数+1
        redis.call('expire', key, releaseTime); -- 设置有效期
        return 1; -- 返回结果
    end;
    return 0; -- 代码走到这里 说明获取锁的不是自己，获取锁失败
    * */

    /*
    * unlock
    * local key = KEYS[1]; -- 第一个参数 锁的KEY
        local threadId = ARGV[1]; -- 第二个参数 线程唯一标识

        if (redis.call('hexists', key, threadId) == 0) then -- 判断当前锁是否还是被自己持有
            return nil; --如果已经不是自己 则直接返回
        end;

        local count = redis.call('hincrby', key, threadId, -1); -- 是自己的锁 则重入次数 -1

        if (count == 0) then -- 判断是否重入次数已经为0
            redis.call('del', key); -- 等于0说明可以释放锁 直接删除
            return nil;
        end;
    * */
}
