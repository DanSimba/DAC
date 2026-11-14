package com.monsterbank.ms_account.config;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
@EnableJpaRepositories(
    basePackages = {
        "com.monsterbank.ms_account.account",
        "com.monsterbank.ms_account.events",
        "com.monsterbank.ms_account.operations"
    },
    entityManagerFactoryRef = "writeEntityManagerFactory",
    transactionManagerRef = "writeTransactionManager"
)
public class WriteDatabaseConfig {

    @Primary
    @Bean
    @ConfigurationProperties("spring.datasource")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Primary
    @Bean
    public DataSource writeDataSource() {
        return writeDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Primary
    @Bean
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory() {
        Map<String, Object> properties = new HashMap<>();
        properties.put("hibernate.hbm2ddl.auto", "update");

        HibernateJpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();

        LocalContainerEntityManagerFactoryBean factory =
            new LocalContainerEntityManagerFactoryBean();

        factory.setDataSource(writeDataSource());
        factory.setPackagesToScan(
            "com.monsterbank.ms_account.account",
            "com.monsterbank.ms_account.events",
            "com.monsterbank.ms_account.operations"
        );
        factory.setJpaVendorAdapter(vendorAdapter);
        factory.setJpaPropertyMap(properties);
        factory.setPersistenceUnitName("write");

        return factory;
    }

    @Primary
    @Bean
    public PlatformTransactionManager writeTransactionManager() {
        JpaTransactionManager transactionManager = new JpaTransactionManager();
        transactionManager.setEntityManagerFactory(
            writeEntityManagerFactory().getObject()
        );
        return transactionManager;
    }
}
