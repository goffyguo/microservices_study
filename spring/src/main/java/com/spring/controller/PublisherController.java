package com.spring.controller;

import com.spring.event.ListenEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:23
 */

@RestController
@RequestMapping("/publishSubscribe/test")
public class PublisherController {

  @Autowired
  private ApplicationContext applicationContext;

  @RequestMapping("publish")
  public void save() {
    ListenEvent event = new ListenEvent(this);
    event.setIdNumber("123123213");
    event.setName("zhangsan");
    event.setMessage("测试发布订阅功能");
    applicationContext.publishEvent(event);
    System.out.println("....................over........................");
  }


}
