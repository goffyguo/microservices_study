package com.spring.subscriber;

import com.alibaba.fastjson.JSON;
import com.spring.event.ListenEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:25
 */
@Component
public class SubscriberTwo implements ApplicationListener<ListenEvent> {

  @Async
  @Override
  public void onApplicationEvent(ListenEvent listenEvent) {
    System.out.println("订阅者2"+ JSON.toJSON(listenEvent).toString());
  }
}
