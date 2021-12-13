package com.goffy.diners.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.goffy.commons.model.dto.DinersDTO;
import com.goffy.commons.model.pojo.Diners;
import org.apache.ibatis.annotations.Mapper;

/**
 * Created with IntelliJ IDEA.
 * @Author: GuoFei
 * @Date: 2021/12/13/15:34
 * @Description: 
 */
@Mapper
public interface DinersMapper extends BaseMapper<Diners> {
    Diners selectByUsername(String trim);

    void save(DinersDTO dinersDTO);

    Diners selectByPhone(String phone);
}