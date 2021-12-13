package com.goffy.aouth.service;

import com.goffy.commons.constant.ApiConstant;
import com.goffy.commons.model.domain.SignInIdentity;
import com.goffy.commons.model.pojo.Diners;
import com.goffy.commons.utils.AssertUtil;
import org.springframework.beans.BeanUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 登录校验
 * @author GuoFei
 */
@Service
public class UserService implements UserDetailsService {

    @Resource
    private JdbcTemplate jdbcTemplate;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AssertUtil.isNotEmpty(username, "请输入用户名");
        return jdbcTemplate.queryForObject(ApiConstant.SELECT_BY_ACCOUNT_INFO, ((resultSet, i)->{
            if (resultSet.wasNull()){
                throw new UsernameNotFoundException("username:" + username + "not found");
            }
            Diners diners = new Diners();
            diners.setUsername(resultSet.getString("username"));
            diners.setNickname(resultSet.getString("nickname"));
            diners.setPassword(resultSet.getString("password"));
            diners.setPhone(resultSet.getString("phone"));
            diners.setEmail(resultSet.getString("email"));
            diners.setAvatarUrl(resultSet.getString("avatar_url"));
            diners.setRoles(resultSet.getString("roles"));
            diners.setIsValid(resultSet.getInt("is_valid"));
            SignInIdentity signInIdentity = new SignInIdentity();
            BeanUtils.copyProperties(diners, signInIdentity);
            return signInIdentity;
        }),username);
    }
}
