package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class UserFundVO implements Serializable {

    //序号
    private Integer number;
    //岗位
    private String title;
    //姓名
    private String name;
    //入职日期
    private LocalDate localDate;
    //基本工资
    private BigDecimal baseMoney;
    //实发基本工资
    private BigDecimal actualBaseMoney;
    //应发绩效工资
    private BigDecimal shouldPerformanceMoney;
    //实发绩效工资
    private BigDecimal actualPerformanceMoney;
    //锁单目标
    private BigDecimal lockTarget;
    //锁单量
    private Integer lockNumber;
    //锁单完成率
    private BigDecimal lockCompleteRate;
    //锁单提成
    private BigDecimal lockDeductMoney;
    //交付量
    private Integer deliveryNumber;
    //交付量计提
    private BigDecimal deliveryNumberProvision;
    //交付量组长计提
    private BigDecimal deliveryNumberMasterProvision;
    //现车奖励
    private BigDecimal directCarSellPrice;
    //往期交付数量
    private Integer prevDeliveryNumber;
    //往期交付提成
    private BigDecimal prevDeliveryMoney;
    //应出勤天数
    private Integer shouldWorkDays;
    //实际出勤天数
    private Integer actualWorkDays;
    //请假天数
    private Integer leaveDays;
    //考勤扣款
    private BigDecimal attendanceCharge;
    //餐补
    private BigDecimal mealMoney;
    //交通补贴
    private BigDecimal trafficMoney;
    //应发工资
    private BigDecimal shouldMoney;
    //社保扣款
    private BigDecimal socialSecurityCharge = BigDecimal.ZERO;
    //公积金扣款
    private BigDecimal providentCharge = BigDecimal.ZERO;
    //个人所得所
    private BigDecimal personalTax = BigDecimal.ZERO;
    //实发工资
    private BigDecimal actualMoney;
    //备注
    private String remark;

    /**

     店长计提：
        实际锁单量 / 目标锁单量  x  400/台   x 台数 = 计提


     */
}
