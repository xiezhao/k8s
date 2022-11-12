package org.example.config;

import org.springframework.data.jpa.domain.Specification;

import java.util.List;

/**
 * @author sunmin
 * @version 1.0.0
 * @Description TODO
 * @createTime 2019年06月07日
 */
public interface RepositoryTemplate {

    /**
     * 得到一个实体对象的实例
     * @param clazz 实体对象类
     * @param id 主键
     * @return 实体对象的实例
     */
    <T,ID> T  get(Class<T> clazz,   ID id);

    /**
     * 得到 实体对象的列表
     * @param clazz 实体对象类
     * @param specification 条件
     * @param <T> entityClass
     * @return 实体对象的列表
     */
    <T> List<T> list(Class<T> clazz, Specification<T> specification);

    /**
     * 得到 实体对象的列表
     * @param clazz 实体对象类
     * @param offSet 从第几条记录开始，从1开始算
     * @param pageSize 每页条数
     * @param specification 条件
     * @param <T>
     * @return 实体对象的列表
     */
    <T> List<T> list(Class<T> clazz, int offSet, int pageSize, Specification<T> specification);

    /**
     * 存储一个实体
     * @param pojo 实体对象实例
     */
    <T> void save(T pojo);

    /**
     * merge 合并一个实体, 比如，当pojo被显示层传过来，不受管理，需要merge
     * @param pojo
     */
    <T> void merge(T pojo);

    /**
     * 删除一个实体
     * @param pojo 实体对象实例
     */
    <T> void delete(T pojo);

    /**
     * 对属性求和（单字段）
     * @param rootClass 属性所在对实体类
     * @param sumClass 求和的属性类型，必须是Number的子类
     * @param propName 求和的属性名称
     * @param specification 条件
     * @param <T> 实体类
     * @param <R> 求和属性类
     * @return 求和值
     */
    <T, R extends Number> R sum(Class<T> rootClass, Class<R> sumClass, String propName, Specification<T> specification);

    /**
     * 求数量
     * @param rootClass 实体对象
     * @param specification 条件
     * @param <T> 实体对象类
     * @return 数量
     */
    <T> Long count(Class<T> rootClass, Specification<T> specification);
}
