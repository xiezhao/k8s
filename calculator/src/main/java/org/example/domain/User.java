package org.example.domain;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "user")
@Data
@NoArgsConstructor
public class User extends AbstractDomainEntity implements Serializable {

    private String name;

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @ManyToOne
    private User master;

    //每日餐补
    private BigDecimal mealMoneyOneDay = BigDecimal.ZERO;
    //交通补贴
    private BigDecimal trafficMoney = BigDecimal.ZERO;
    //基本工资
    private BigDecimal baseMoney = BigDecimal.ZERO;
    //入职时间
    private LocalDate entryTime;
    //离职时间
    private LocalDate departureTime;

    private BigDecimal prevDeliveryMoney = BigDecimal.ZERO;

    //身份证号
    private String idCard;

    @AllArgsConstructor
    public enum UserType {
        MANAGER("店长"),
        MASTER("组长"),
        USER_MANAGER("用户主理"),
        USER_DELIVERY("交付专员");
        private String desc;
    }

    @Override
    protected String idPrefix() {
        return "US";
    }
}
