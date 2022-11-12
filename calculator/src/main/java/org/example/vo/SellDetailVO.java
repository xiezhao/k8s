package org.example.vo;

import lombok.Data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SellDetailVO implements Serializable {

    private String managerName;

    private List<SellDetail> list;

   @Data
    public static class SellDetail {
        private String name;
        //新增锁单
        private Integer lockNumber;
        //提成阶梯
        private BigDecimal ladder;
        //锁单提成
        private BigDecimal lockMoney;
        //当月交车数量
        private Integer deliveryNumber;
        //当月交车提成
        private BigDecimal deliveryMoney;
        //未交数量
        private Integer notDeliveryNumber;
        //订单提成
        private BigDecimal orderMoney;
        //现车奖励
        private BigDecimal directCarSellPrice;
        //往期交付数量
        private Integer prevDeliveryNumber;
        //往期交付提成
        private BigDecimal prevDeliveryMoney;
        //合计提成
        private BigDecimal totalDeductMoney;
        //底薪
        private BigDecimal baseMoney;
        //合计
        private BigDecimal totalMoney;

    }


}
