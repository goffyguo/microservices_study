package com.goffy.diners.service;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;
import com.goffy.commons.exception.ParameterException;
import com.goffy.commons.model.vo.SignInDinerInfo;
import com.goffy.commons.utils.AssertUtil;
import org.springframework.data.redis.connection.BitFieldSubCommands;
import org.springframework.data.redis.connection.ReactiveStringCommands;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2022/01/04/14:11
 * @Description: 签到业务类
 * 需求：用户签到，默认是当天，但可以通过传入日期进行日期补签，返回用户连续签到次数
 */
@Service
public class SignInService {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * Bitmaps 叫位图，他不是Redis的基本数据类型，而是基于String数据类型的按位操作，高阶数据类型的一种。
     * Bitmaps 最大支持数为2^32位，使用512M内存就可以存储多大42.9亿的字节信息（2^32=4294967196）。
     * <p>
     * 由一组bit组成，每个bit对应0和1，虽然内部还是采用String类型存储，
     * 但是Redis提供了一些指令用于直接操作位图，可以把它看作是一个bit数组，数据的下标就是偏移量。
     * 它的优点是内存开销小、效率高且操作简单，很适合用于签到这类场景。
     */

    public int doSign(String accessToken, String dateStr) {
        // 获取登录用户信息
        SignInDinerInfo signInDinerInfo = loadSignDinerInfo(accessToken);
        int dinerId = 1;
        if (signInDinerInfo == null) {
            dinerId = 1;
        }
        // 获取日期
        Date date = getDate(dateStr);
        // 获取日期对应的天数，多少号 // 从 0 开始
        int offset = DateUtil.dayOfMonth(date) - 1;
        // 构建 Key user:sign:5:yyyyMM
        String signKey = buildSignKey(dinerId, date);
        // 查看是否已签到
        boolean isSigned = redisTemplate.opsForValue().getBit(signKey, offset);
        AssertUtil.isTrue(isSigned, "当前日期已完成签到，无需再签");
        // 签到
        redisTemplate.opsForValue().setBit(signKey, offset, true);
        // 统计连续签到的次数
        int count = getContinuousSignCount(dinerId, date);
        return count;
    }


    /**
     * 统计连续签到的次数
     * @param dinerId
     * @param date
     * @return
     */
    private int getContinuousSignCount(int dinerId, Date date) {
        // 获取日期对应的天数，多少号，假设是30
        int dayOfMonth = DateUtil.dayOfMonth(date);
        // 构建 key
        String buildSignKey = buildSignKey(dinerId, date);
        BitFieldSubCommands commands = BitFieldSubCommands.create()
                .get(BitFieldSubCommands.BitFieldType.unsigned(dayOfMonth))
                .valueAt(0);
        List<Long> list = redisTemplate.opsForValue().bitField(buildSignKey, commands);
        if (list == null || list.isEmpty()){
            return 0;
        }
        int signCount = 0;
        long v = list.get(0)==null?0:list.get(0);
        // i 表示位移操作次数
        for (int i = dayOfMonth; i > 0; i++) {
            // 右移再左移，如果等于自己说明最低位是 0，表示未签到
            if (v >> 1 << 1 == v){
                // 低位 0 且非当天说明连续签到中断了
                if (i!= dayOfMonth) break;
            }else {
                signCount++;
            }
            // 右移一位并重新赋值，相当于把最低位丢弃一位
            v >>= 1;
        }
        return signCount;
    }

    /**
     * 构建key user:sign:5:yyyyMM
     * @param dinerId
     * @param date
     * @return
     */
    private String buildSignKey(int dinerId, Date date) {
        return String.format("user:sign:%d:%s", dinerId, DateUtil.format(date, "yyyyMM"));
    }

    /**
     * 获取签到日期
     * @param dateStr
     * @return
     */
    private Date getDate(String dateStr) {
        if (StrUtil.isBlank(dateStr)){
            return new Date();
        }
        return DateUtil.parseDate(dateStr);
    }

    /**
     * 获取登录用户
     *
     * @param accessToken
     * @return
     */
    private SignInDinerInfo loadSignDinerInfo(String accessToken) {
        return null;
    }

}
