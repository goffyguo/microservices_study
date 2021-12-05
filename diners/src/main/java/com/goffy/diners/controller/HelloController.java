package com.goffy.diners.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/03/16:50
 * @Description:
 */
@RestController
public class HelloController {

    @GetMapping("/hello")
    public String hello(String name){
        return "hello" + name;
    }

}
