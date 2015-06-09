package com.emc.cloudfoundry.notification.orphan;

import java.util.Properties;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

public abstract class AbstractJpaRepositoryConfig {

    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(DataSource dataSource) {
        return createEntityManagerFactoryBean(dataSource, getJpaVendorAdapter());
    }

    @Bean(name = "transactionManager")
    public JpaTransactionManager transactionManager(EntityManagerFactory entityManagerFactory) {
        return new JpaTransactionManager(entityManagerFactory);
    }

    protected abstract JpaVendorAdapter getJpaVendorAdapter();

    protected abstract String getPackagesToScan();

    protected LocalContainerEntityManagerFactoryBean createEntityManagerFactoryBean(DataSource dataSource, JpaVendorAdapter jpaVendor) {

        LocalContainerEntityManagerFactoryBean factory = new LocalContainerEntityManagerFactoryBean();
        factory.setDataSource(dataSource);
        factory.setPackagesToScan(getPackagesToScan());
        factory.setJpaVendorAdapter(jpaVendor);
        factory.setJpaProperties(getAdditionalProperties());
        return factory;
    }

    protected Properties getAdditionalProperties() {
        Properties properties = new Properties();
        return properties;
    }

}
