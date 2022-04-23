package com.webapi;

import java.util.ArrayList;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 
 * @author: GuoFei
 * @date: 2022-04-23 10:47
 * 
 */

public enum StatisticsTypeEnum {
  ACTIVE_SPREAD("PASDS","广告推广的曝光和点击量统计"),
  ACTIVE_SPREAD1("CESHI","广告推广的曝光和点击量统计");

  private final String code;
  private final String message;

  private StatisticsTypeEnum(String code,String message){
    this.code = code;
    this.message = message;
  }

  public String getCode() {
    return code;
  }

  public String getMessage() {
    return message;
  }

  public static StatisticsTypeEnum findPushType(String code){
    for (StatisticsTypeEnum value : values()) {
      if (value.getCode() == code){
        return value;
      }
    }
    return null;
  }

  public static List<StatisticsTypeEnum> getAllPushType(){
    ArrayList<StatisticsTypeEnum> statisticsTypeEnums = new ArrayList<>();
    for (StatisticsTypeEnum value : values()) {
      statisticsTypeEnums.add(value);
    }
    return statisticsTypeEnums;
  }

  public static List<String> getAllPushCode(){
    ArrayList<String> strings = new ArrayList<>();
    for (StatisticsTypeEnum value : values()) {
      strings.add(value.getCode());
    }
    return strings;
  }

}
