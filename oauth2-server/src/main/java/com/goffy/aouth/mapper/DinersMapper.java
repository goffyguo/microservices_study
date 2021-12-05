package com.goffy.aouth.mapper;

import com.goffy.commons.model.pojo.Diners;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/03/17:48
 * @Description: DinersMapper
 */

public interface DinersMapper {

    /**
     * 根据用户名 or 手机号 or 邮箱查询用户信息
     * @param account
     * @return
     */
    @Select("select id, username, nickname, phone, email, " +
            "password, avatar_url, roles, is_valid from t_diners where " +
            "(username = #{account} or phone = #{account} or email = #{account})")
    Diners selectByAccountInfo(@Param("account") String account);
}
