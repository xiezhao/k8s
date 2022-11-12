package org.example.config;

import java.util.EnumSet;

/**
 * @author sunmin
 * @version 1.0.0
 * @ClassName EntityState.java
 * @Description TODO
 * @createTime 2019年07月08日
 */
public enum EntityState {

    NORMAL(0), DELETED(1), LIMITED(2), SYSTEM(128);

    private int state;

    EntityState(int state)
    {
        this.state = state;
    }

    public int getState()
    {
        return state;
    }

    private static EnumSet<EntityState> set = EnumSet.allOf(EntityState.class);

    public static EntityState valueOf(int state)
    {
        for (EntityState enum1 : set)
        {
            if (enum1.getState() == state)
                return enum1;
        }
        throw new IllegalArgumentException("未知的枚举值：" + state);
    }
}
