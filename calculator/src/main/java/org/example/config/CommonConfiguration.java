package org.example.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.DependsOn;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import javax.persistence.EntityManager;

@Configuration
public class CommonConfiguration {


    @Bean
    @DependsOn("repositoryTemplateJpa")
    @ConditionalOnMissingBean({RepositoryTemplateFactory.class})
    public RepositoryTemplateFactory repositoryTemplateFactory(RepositoryTemplateJpa repositoryTemplateJpa,
                                                               EntityManager entityManager,
                                                               ApplicationContext applicationContext) {
        RepositoryTemplateFactory repositoryTemplateFactory = new RepositoryTemplateFactory();
        repositoryTemplateFactory.setRepositoryTemplate(repositoryTemplateJpa);
        repositoryTemplateFactory.setRepositoryTemplateJpa(repositoryTemplateJpa);
        repositoryTemplateFactory.setApplicationContext(applicationContext);
        repositoryTemplateFactory.setEntityManager(entityManager);
        return repositoryTemplateFactory;
    }

    @Bean
    @Transactional(readOnly = true)
    @ConditionalOnMissingBean({RepositoryTemplateJpa.class})
    public RepositoryTemplateJpa repositoryTemplateJpa(EntityManager entityManager) {
        RepositoryTemplateJpa repositoryTemplateJpa = new RepositoryTemplateJpa();
        repositoryTemplateJpa.setEntityManager(entityManager);
        return repositoryTemplateJpa;
    }


    @Bean(name = "multipartResolver")
    public MultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        // resolveLazily属性启用是为了推迟文件解析，以在在UploadAction中捕获文件大小异常
        resolver.setResolveLazily(true);
        resolver.setMaxInMemorySize(40960);
        // 上传文件大小 5G
        resolver.setMaxUploadSize(5 * 1024 * 1024 * 1024);
        return resolver;
    }

}
