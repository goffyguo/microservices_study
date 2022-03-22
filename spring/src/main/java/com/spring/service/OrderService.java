package com.spring.service;

import com.spring.pojo.Order;
import javax.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:06
 */
@Service
public class OrderService {

  @Resource
  private ApplicationContext applicationContext;

  public void addOrder(){
    Order order = new Order();
    order.setOrderNo("20220322");
    order.setContent("下单（买书）");
    System.out.println("下订单: " + order);
    applicationContext.publishEvent(order);
  }



}
