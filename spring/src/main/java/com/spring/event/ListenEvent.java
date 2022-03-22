package com.spring.event;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.context.ApplicationEvent;

/**
 * @author: GuoFei
 * @date: 2022-03-22 15:20
 */
@Data
public class ListenEvent extends ApplicationEvent {

  private String idNumber;

  private String name;

  private String message;

  public ListenEvent(Object source) {
    super(source);
  }

}
