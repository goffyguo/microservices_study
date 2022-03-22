package com.spring.service;

import com.spring.pojo.Order;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:10
 */

@Service
public class MsgService {

  @EventListener
  public void sendMessage(Order or){
    System.out.println("发送消息："+or);
  }

}
