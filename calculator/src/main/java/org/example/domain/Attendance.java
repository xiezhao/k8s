package org.example.domain;


import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.math.BigDecimal;

/**
 * 考勤
 */
@Table(name = "attendance")
@Entity
@Data
@NoArgsConstructor
public class Attendance extends AbstractDomainEntity {

    private Integer year;

    private Integer month;
    //应出勤天数
    private BigDecimal planWorkDays;
    //实际出勤天数
    private BigDecimal actualWorkDays;
    //请假天数
    private BigDecimal leaveDays;

    @ManyToOne
    private User user;

    //社保扣款
    private BigDecimal socialSecurityCharge = BigDecimal.ZERO;
    //公积金扣款
    private BigDecimal providentCharge = BigDecimal.ZERO;
    //个人所得所
    private BigDecimal personalTax = BigDecimal.ZERO;

    private String idCard;


    public Attendance(Integer year, Integer month, User user) {
        this.year = year;
        this.month = month;
        this.user = user;
        this.idCard = user.getIdCard();
    }

    @Override
    protected String idPrefix() {
        return "AT";
    }
}
