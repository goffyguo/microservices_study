package com.webapi.service;

import com.webapi.StatisticsTypeEnum;
import com.webapi.basic.StatisticsCountServiceBasics;
import java.util.Date;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 
 * @author: GuoFei
 * @date: 2022-04-23 10:45
 * 
 */
@Service
@Slf4j
public class TestStatisticsService implements StatisticsCountServiceBasics {

  /**
   * 日统计
   *
   * @param beginTime
   * @param endTime
   * @return
   */
  @Override
  public boolean dayStatistics(String beginTime, String endTime) {
    log.info("---dayStatistics---");
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
    log.info("---weekStatistics---");
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
    log.info("---monthStatistics---");
    return false;
  }

  /**
   * 自定义type，最终要根据此type来区别具体实现类
   *
   * @return
   */
  @Override
  public String getImplType() {
    log.info("---getImplType---");
    return StatisticsTypeEnum.ACTIVE_SPREAD.getCode();
  }
}
