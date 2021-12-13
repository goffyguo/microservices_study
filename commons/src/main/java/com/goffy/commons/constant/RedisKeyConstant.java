package com.goffy.commons.constant;

import lombok.Getter;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/13/15:12
 * @Description:
 */
@Getter
public enum RedisKeyConstant {

    verify_code("verify_code:", "验证码");

    private String key;
    private String desc;

    RedisKeyConstant(String key, String desc) {
        this.key = key;
        this.desc = desc;
    }
}
