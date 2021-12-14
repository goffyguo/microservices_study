package com.goffy.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goffy.seckill.pojo.SeckillVouchers;
import com.goffy.seckill.pojo.SeckillVouchersDTO;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * @Author: GuoFei
 * @Date: 2021/12/14/15:11
 * @Description: 
 */
@Mapper
public interface SeckillVouchersMapper extends BaseMapper<SeckillVouchers> {
    SeckillVouchers selectVoucher(Integer fkVoucherId);

    void save(SeckillVouchersDTO seckillVouchers);

    int stockDecrease(Integer id);
}