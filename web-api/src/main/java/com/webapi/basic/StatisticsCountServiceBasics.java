package com.webapi.basic;

import java.util.Date;
import lombok.Data;

/**
 * @author: GuoFei
 * @date: 2022-04-23 09:59
 */
public interface StatisticsCountServiceBasics {


  /**
   * 日统计
   * @param beginTime
   * @param endTime
   * @return
   */
  boolean dayStatistics(String beginTime, String endTime);


  /**
   * 周统计
   * @param beginTime
   * @param endTime
   * @return
   */
  boolean weekStatistics(Date beginTime, Date endTime);


  /**
   * 月统计
   * @param beginTime
   * @param endTime
   * @return
   */
  boolean monthStatistics(Date beginTime, Date endTime);


  /**
   * 自定义type，最终要根据此type来区别具体实现类
   * @return
   */
  String getImplType();



}
