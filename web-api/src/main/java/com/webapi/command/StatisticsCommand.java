package com.webapi.command;

import com.webapi.basic.StatisticsCountServiceBasics;
import com.webapi.domain.bo.StatisticsCountBO;
import com.webapi.util.DateUtils;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * 上下文统一管理调用器
 *
 * @author:
 * @date: 2022-04-23 09:56
 */
@Component
@Slf4j
public class StatisticsCommand implements ApplicationListener<ContextRefreshedEvent> {

  private final ConcurrentHashMap<String, StatisticsCountServiceBasics> processorsMap = new ConcurrentHashMap<>();

  @Resource
  List<StatisticsCountServiceBasics> processors;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent contextRefreshedEvent) {
    if (contextRefreshedEvent.getApplicationContext()
        .getParent() ==null) {
      if (processors == null || processors.size() == 0) {
        return;
      }
      for (StatisticsCountServiceBasics processor : processors) {
        if (processor.getImplType() == null || processor.getImplType().isEmpty()) {
          log.error("---->springboot启动时监测到存在有定时任务为空的 implType-->{},请设置后重新启动",
              processor.getClass().getSimpleName());
          showdown(
              ((ConfigurableApplicationContext) contextRefreshedEvent.getApplicationContext()));
          return;
        }
        if (processorsMap.containsKey(processor.getImplType())) {
          log.error("---->springboot启动时监测到存在重复定时任务的 implType-->{},请设置唯一后重新启动",
              processor.getImplType());
          showdown(
              ((ConfigurableApplicationContext) contextRefreshedEvent.getApplicationContext()));
          return;
        }
        processorsMap.put(processor.getImplType(), processor);
      }
    }
  }

  public StatisticsCountServiceBasics getProcessors(String implType) {
    return processorsMap.get(implType);
  }


  public String statistics(StatisticsCountBO statisticsCountBO) {
    StatisticsCountServiceBasics processors = this.getProcessors(statisticsCountBO.getImplType());
    if (processors == null) {
      return "error:ImplType 参数有误，未找到对应统计";
    }
    String beginTime = statisticsCountBO.getBeginTime();
    String endTime = statisticsCountBO.getEndTime();
    boolean result = false;
    switch (statisticsCountBO.getDateType()) {
      case 0:
        if (beginTime == null || beginTime.trim().isEmpty()) {
          beginTime = DateUtils.dateConvertString(DateUtils.getYesterdayMorning(), 0);
        }
        if (endTime == null || endTime.trim().isEmpty()) {
          endTime = DateUtils.dateConvertString(DateUtils.getYesterdayNight(), 0);
        }
        result = processors.dayStatistics(beginTime + "00:00:00", endTime + "23:59:59");
        break;
      case 1:
        if (beginTime == null || beginTime.trim().isEmpty() || endTime == null || endTime.trim()
            .isEmpty()) {
          Date start = DateUtils.getWeekStartDayPre();
          Date end = DateUtils.getWeekEndDayPre();
          result = processors.weekStatistics(start, end);
          break;
        }
        result = processors.weekStatistics(DateUtils.stringConvertDate(beginTime, 0),
            DateUtils.stringConvertDate(endTime, 0));
        break;
      case 2:
        if (beginTime == null || beginTime.trim().isEmpty() || endTime == null || endTime.trim()
            .isEmpty()) {
          Date start = DateUtils.getMonthStartDayPre();
          Date end = DateUtils.getMonthEndDayPre();
          //result = processors.monthStatistics(start, end);
          break;
        }
        //result = processors.monthStatistics(DateUtils.stringConvertDate(beginTime, 0),DateUtils.stringConvertDate(endTime, 0));
        break;
      default:
        return "error: dateType只能是0（日）,1（周）,2（月）";
    }
    return result ? "success" : "error:统计失败";
  }


  private void showdown(ConfigurableApplicationContext context) {
    if (null != context) {
      context.close();
    }
  }
}
