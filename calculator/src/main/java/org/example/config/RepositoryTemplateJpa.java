package org.example.config;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.support.JpaEntityInformationSupport;
import org.springframework.data.repository.core.EntityInformation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.List;

public class RepositoryTemplateJpa implements RepositoryTemplate, org.springframework.data.repository.Repository {

    static final Integer MAX_NUMBER_PAGE = 999999;

    private EntityManager entityManager;

    @Override
    public <T, ID> T get(Class<T> clazz, ID id) {
        return entityManager.find(clazz, id);
    }

    @Override
    public <T> List<T> list(Class<T> clazz, Specification<T> specification) {
        return list(clazz, 1, MAX_NUMBER_PAGE, specification);
    }

    @Override
    public <T> List<T> list(Class<T> clazz, int offSet, int pageSize, Specification<T> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<T> query = builder.createQuery(clazz);
        Root<T> root = query.from(clazz);
        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, builder);
            if (predicate != null) {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.and(predicate, builder.equal(root.get("entityState"), EntityState.NORMAL)));
                } else {
                    query.where(predicate);
                }

            } else {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
                }
            }
        } else {
            if (VirtualDelete.class.isAssignableFrom(clazz)) {
                query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
            }
        }
        query.select(root);
        TypedQuery typedQuery = entityManager.createQuery(query);
        typedQuery.setFirstResult(offSet-1);
        typedQuery.setMaxResults(pageSize);
        NamedEntityGraph namedEntityGraph = clazz.getAnnotation(NamedEntityGraph.class);
        if(namedEntityGraph != null) {
            EntityGraph<?> entityGraph = entityManager.createEntityGraph(namedEntityGraph.name());
            typedQuery.setHint("javax.persistence.fetchgraph", entityGraph);
        }
        return typedQuery.getResultList();
    }

    @Override
    @Transactional
    public <T> void save(T pojo) {
        entityManager.persist(pojo);
    }


    @Override
    @Transactional
    public <T> void merge(T pojo) {
        entityManager.merge(pojo);
    }

    @Override
    @Transactional
    public <T> void delete(T pojo) {
        entityManager.remove(entityManager.merge(pojo));
    }


    @Transactional
    public Object execute(EmCallback callback, Object... objects) {
        return callback.doInEntiyManager(entityManager);
    }

    /**
     * 没有分页，谨慎使用
     * @param jpql
     * @param params
     * @param <T>
     * @return
     */
    public <T> List<T> find(String jpql, Object... params) {

        Query query = entityManager.createQuery(jpql);
        if (params != null) {
            for (int i = 0; i < params.length; i++)
                query.setParameter(i + 1, params[i]);

        }
        return query.getResultList();

    }

    public <T> EntityInformation<T, ?> getEntityInformation(Class<T> tClass) {
        return JpaEntityInformationSupport.getEntityInformation(tClass, entityManager);
    }

    public  void flush() {
        entityManager.flush();
    }

    public <T> void refresh(T entity) {
        entityManager.refresh(entity);
    }

    public EntityManager getEntityManager() {
        return entityManager;
    }

    public void setEntityManager(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * 对属性求和（单字段）
     * @param clazz 属性所在对实体类
     * @param sumClass 求和的属性类型，必须是Number的子类
     * @param propName 求和的属性名称
     * @param specification 条件
     * @param <T> 实体类
     * @param <R> 求和属性类
     * @return 求和值
     */
    @Override
    public <T, R extends Number> R sum(Class<T> clazz, Class<R> sumClass, String propName, Specification<T> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<R> query = builder.createQuery(sumClass);
        Root<T> root = query.from(clazz);
        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, builder);
            if (predicate != null) {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.and(predicate, builder.equal(root.get("entityState"), EntityState.NORMAL)));
                } else {
                    query.where(predicate);
                }

            } else {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
                }
            }
        } else {
            if (VirtualDelete.class.isAssignableFrom(clazz)) {
                query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
            }
        }
        TypedQuery<R> sum = entityManager.createQuery(query.select(builder.sum(root.get(propName))));
        return sum.getSingleResult();
    }

    /**
     * 求数量
     * @param clazz 实体对象
     * @param specification 条件
     * @param <T> 实体对象类
     * @return 数量
     */
    @Override
    public <T> Long count(Class<T> clazz, Specification<T> specification) {
        CriteriaBuilder builder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> query = builder.createQuery(Long.class);
        Root<T> root = query.from(clazz);
        if (specification != null) {
            Predicate predicate = specification.toPredicate(root, query, builder);
            if (predicate != null) {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.and(predicate, builder.equal(root.get("entityState"), EntityState.NORMAL)));
                } else {
                    query.where(predicate);
                }

            } else {
                if (VirtualDelete.class.isAssignableFrom(clazz)) {
                    query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
                }
            }
        } else {
            if (VirtualDelete.class.isAssignableFrom(clazz)) {
                query.where(builder.equal(root.get("entityState"), EntityState.NORMAL));
            }
        }
        TypedQuery<Long> sum = entityManager.createQuery(query.select(builder.count(root)));
        return sum.getSingleResult();
    }

}
