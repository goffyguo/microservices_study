package com.goffy.commons;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/03/17:22
 * @Description:
 */
public class ApiConstant {
    // 成功
    public static final int SUCCESS_CODE = 1;
    // 成功提示信息
    public static final String SUCCESS_MESSAGE = "Successful.";
    // 错误
    public static final int ERROR_CODE = 0;
    // 未登录
    public static final int NO_LOGIN_CODE = -100;
    // 请登录提示信息
    public static final String NO_LOGIN_MESSAGE = "Please login.";
    // 错误提示信息
    public static final String ERROR_MESSAGE = "Oops! Something was wrong.";

    public static final String SELECT_BY_ACCOUNT_INFO =
                    "select id, username, nickname, phone, email, " +
                    "password, avatar_url, roles, is_valid from t_diners where " +
                    "username = ?";
}
