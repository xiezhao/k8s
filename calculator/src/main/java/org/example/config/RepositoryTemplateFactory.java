package org.example.config;

import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;

import javax.persistence.EntityManager;

/**
 * @author sunmin
 * @version 1.0.0
 * @createTime 2019年06月07日
 */
public class RepositoryTemplateFactory {

    private static RepositoryTemplate repositoryTemplate;

    private static RepositoryTemplateJpa repositoryTemplateJpa;

    private static EntityManager entityManager;

    private static ApplicationContext applicationContext;

    /**
     * get repository template interface of only methods of interface
     * @return repositoryTemplate
     */
    public static RepositoryTemplate getRepositoryTemplate() {
        return repositoryTemplate;
    }

    /**
     * get a realization of spring Jpa, has the special methods
     * @return
     */
    public static RepositoryTemplateJpa getRepositoryTemplateJpa() {
        return repositoryTemplateJpa;
    }


    /**
     * get JpaRepository from applicationContext
     *
     * @param jpaRepositoryClass
     * @param <T>
     * @return
     */
    public static <T extends JpaRepository> T getJpaRepository(Class<T> jpaRepositoryClass) {
        return applicationContext.getBean(jpaRepositoryClass);
    }

    public void setRepositoryTemplate(RepositoryTemplate repositoryTemplate) {
        RepositoryTemplateFactory.repositoryTemplate = repositoryTemplate;
    }

    public void setRepositoryTemplateJpa(RepositoryTemplateJpa repositoryTemplateJpa) {
        RepositoryTemplateFactory.repositoryTemplateJpa = repositoryTemplateJpa;
    }

    public void setEntityManager(EntityManager em) {
        entityManager = em;
    }

    public void setApplicationContext(ApplicationContext context) {
        applicationContext = context;
    }

}
