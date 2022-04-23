package com.webapi;

import com.webapi.command.StatisticsCommand;
import com.webapi.domain.bo.StatisticsCountBO;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * 数据统计统一入口类
 * @author: GuoFei
 * @date: 2022-04-23 09:46
 */

@RestController
public class StatisticsController {

  @Resource
  private StatisticsCommand statisticsCommand;


  @RequestMapping("/unify")
  @ResponseBody
  public String unifyStatistics(String implType, Integer dateType, String beginTime, String endTime){
    if (implType == null || implType.isEmpty()){
      return "implType 不能为空";
    }
    if (dateType == null){
      return "dateType 不能为空";
    }
    StatisticsCountBO statisticsCountBO = new StatisticsCountBO(implType,dateType,beginTime,endTime);
    return statisticsCommand.statistics(statisticsCountBO);
  }
}
