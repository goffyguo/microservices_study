package com.goffy.commons.model.pojo;

import com.goffy.commons.model.base.BaseModel;
import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author: GuoFei
 * @date: 2022-03-19 13:56
 */
@Data
@ApiModel(description = "Feed信息类")
public class Feeds  extends BaseModel {

  /**
   * 内容
   */
  private String content;

  /**
   * 食客ID
   */
  private String fkDinerId;

  /**
   * 点赞
   */
  private int praiseAmount;

  /**
   * 评论
   */
  private int commentAmount;

  /**
   * 关联的餐厅
   */
  private Integer fkRestaurantId;


}
