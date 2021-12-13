package com.goffy.diners.vo;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/13/14:03
 * @Description:
 */
@Setter
@Getter
public class LoginDinerInfo implements Serializable {

    private String nickname;
    private String token;
    private String avatarUrl;
}
