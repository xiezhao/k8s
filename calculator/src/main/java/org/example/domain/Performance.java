package org.example.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.example.config.EntityState;
import org.example.config.RepositoryTemplateFactory;
import org.example.repository.PerformanceRepository;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/*
    销售提成情况
 */

@Table(name = "performance")
@Entity
@Getter
public class Performance extends AbstractDomainEntity {

    @Column(name = "`condition`")
    @Enumerated(EnumType.STRING)
    private Condition condition;

    @Column(name = "`number`")
    private Integer number;

    @Enumerated(EnumType.STRING)
    private User.UserType userType;

    @Column(name = "`money`")
    private BigDecimal money;

    @AllArgsConstructor
    enum Condition {
        LT("小于"),
        EQ("等于"),
        LEQ("小于等于"),
        GT("大于"),
        GEQ("大于等于"),
        ;
        private String desc;
    }





    public static BigDecimal getLadder(User.UserType userType, Integer number){
        List<Performance> performanceList = RepositoryTemplateFactory.getJpaRepository(PerformanceRepository.class)
                .findByUserTypeAndEntityState(userType, EntityState.NORMAL);

        performanceList = performanceList.stream().sorted(Comparator.comparing(Performance::getNumber)).collect(Collectors.toList());

        for (Performance performance : performanceList) {
            if (performance.match(number)) {
                return performance.getMoney();
            }
        }
        return null;
    }


    public boolean match(Integer number){
        //传入的小于 this.number 时，看是不是，LT
        if (number < this.number){
            if (Condition.LT.equals(getCondition())) {
                return true;
            }
        } else if (number == this.number) {
            if (Condition.EQ.equals(getCondition())
                    || Condition.LEQ.equals(getCondition())
                    || Condition.GEQ.equals(getCondition())) {
                return true;
            }
        } else {
            if (Condition.GT.equals(getCondition()) || Condition.GEQ.equals(getCondition())) {
                return true;
            }
        }
        return false;
    }

    @Override
    protected String idPrefix() {
        return "PE";
    }
}
