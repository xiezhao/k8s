package org.example.config;

/**
 * @author sunmin
 * @version 1.0.0
 * @ClassName VirtualDelete.java
 * @Description TODO
 * @createTime 2019年07月08日
 */
public interface VirtualDelete {

    EntityState getEntityState();

    void setEntityState(EntityState entityState);

    default Boolean isDeleted() {
        return getEntityState() == EntityState.DELETED;
    }
}
