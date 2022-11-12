package org.example.domain;

import lombok.Data;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/*
    销售
 */
@Table(name = "sell")
@Entity
@Data
public class Sell extends AbstractDomainEntity {

    private Integer year;

    private Integer month;

    @ManyToOne
    private User user;
    //锁单量
    private Integer lockNumber;
    //锁单目标
    private Integer lockNumberTarget;
    //交付量
    private Integer deliveryNumber;
    //往期交付数量
    private Integer prevDeliveryNumber;
    //往期交付提成
    private BigDecimal prevDeliveryMoney;
    //现车销售奖励
    private BigDecimal directCarSellPrice = BigDecimal.ZERO;


    @Override
    protected String idPrefix() {
        return "CE";
    }
}
