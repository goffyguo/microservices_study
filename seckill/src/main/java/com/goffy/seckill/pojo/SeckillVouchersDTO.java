package com.goffy.seckill.pojo;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.goffy.commons.model.base.BaseModel;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 *
 * @Author: GuoFei
 * @Date: 2021/12/14/15:49
 * @Description:
 */
@Setter
@Getter
@ApiModel(description = "抢购代金券信息")
public class SeckillVouchersDTO extends BaseModel {
    @ApiModelProperty("代金券外键")
    private Integer fkVoucherId;
    @ApiModelProperty("数量")
    private int amount;
    @ApiModelProperty("抢购开始时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date startTime;
    @ApiModelProperty("抢购结束时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm", timezone = "GMT+8")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm")
    private Date endTime;
}
