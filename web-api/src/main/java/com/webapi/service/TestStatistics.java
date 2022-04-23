package com.webapi.service;

import com.webapi.basic.StatisticsCountServiceBasics;
import java.util.Date;

/**
 * 
 * @author: GuoFei
 * @date: 2022-04-23 10:45
 * 
 */
public class TestStatistics implements StatisticsCountServiceBasics {

  /**
   * 日统计
   *
   * @param beginTime
   * @param endTime
   * @return
   */
  @Override
  public boolean dayStatistics(String beginTime, String endTime) {
    return false;
  }

  /**
   * 周统计
   *
   * @param beginTime
   * @param endTime
   * @return
   */
  @Override
  public boolean weekStatistics(Date beginTime, Date endTime) {
    return false;
  }

  /**
   * 月统计
   *
   * @param beginTime
   * @param endTime
   * @return
   */
  @Override
  public boolean monthStatistics(Date beginTime, Date endTime) {
    return false;
  }

  /**
   * 自定义type，最终要根据此type来区别具体实现类
   *
   * @return
   */
  @Override
  public String getImplType() {
    return null;
  }
}
