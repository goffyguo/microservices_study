package com.spring;

import com.spring.service.OrderService;
import javax.annotation.Resource;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:12
 */
@SpringBootTest
public class Tests {

  @Resource
  OrderService orderService;
  @Test
  public void testLoads(){
    orderService.addOrder();
  }

}
