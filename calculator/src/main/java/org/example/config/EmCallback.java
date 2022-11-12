package org.example.config;

import javax.persistence.EntityManager;

/**
 * @author sunmin
 * @version 1.0.0
 * @ClassName EmCallback.java
 * @Description TODO
 * @createTime 2019年06月30日
 */
public interface EmCallback {

    Object doInEntiyManager(EntityManager entityManager);
}
