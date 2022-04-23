package com.webapi.domain.bo;

import java.sql.Struct;
import javax.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;


/**
 * 统计的统一入参
 * @author: GuoFei
 * @date: 2022-04-23 09:52
 */
@Data
@AllArgsConstructor
public class StatisticsCountBO {

  @NotNull(message = "implType 统计方式不能为空")
  private String implType;
  @NotNull(message = "dateType 统计类型不能为空")
  private Integer dateType;

  private String beginTime;
  private String endTime;

}
