package com.goffy.others.ddd;

import javax.xml.bind.ValidationException;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/28/14:09
 * @Description:
 */
public interface RegistrationService {

    /**
     * 用户注册
     * @param name
     * @param phone
     * @param address
     * @return
     */
    User register(Name name, PhoneNumber phone, Address address) throws ValidationException;
}
