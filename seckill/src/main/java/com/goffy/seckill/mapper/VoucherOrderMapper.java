package com.goffy.seckill.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goffy.seckill.pojo.VoucherOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * Created with IntelliJ IDEA.
 * @Author: GuoFei
 * @Date: 2021/12/14/16:17
 * @Description: 
 */
@Mapper
public interface VoucherOrderMapper extends BaseMapper<VoucherOrder> {
    VoucherOrder findDinerOrder(@Param("dinerId") Integer id, @Param("voucherId") Integer fkVoucherId);

    long save(VoucherOrder voucherOrders);
}