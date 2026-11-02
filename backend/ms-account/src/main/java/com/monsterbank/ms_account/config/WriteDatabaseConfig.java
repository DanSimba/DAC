package com.monsterbank.ms_account.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;

import com.monsterbank.ms_account.account.*;

import org.springframework.boot.jdbc.autoconfigure.DataSourceProperties;
import org.springframework.boot.jpa.EntityManagerFactoryBuilder;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import jakarta.persistence.EntityManagerFactory;
import org.springframework.context.annotation.Primary;

@Configuration
@EnableJpaRepositories(
    basePackages = "com.monsterbank.ms_account.account.command.repository", //ENCONTRA ENDEREÇO DO REPO CERTO
    entityManagerFactoryRef = "writeEntityManagerFactory", //DECLARA QUE TIPO DE BANCO QUE É
    transactionManagerRef = "writeTransactionManager"
)
public class WriteDatabaseConfig{

    @Bean
    @Primary
    @ConfigurationProperties("app.datasource.write")
    public DataSourceProperties writeDataSourceProperties() {
        return new DataSourceProperties();
    }

    @Bean
    @Primary
    public DataSource writeDataSource() {
        return writeDataSourceProperties()
            .initializeDataSourceBuilder()
            .build();
    }

    @Bean(name = "writeEntityManagerFactory")
    public LocalContainerEntityManagerFactoryBean writeEntityManagerFactory(
        EntityManagerFactoryBuilder builder,
        @Qualifier("writeDataSource") DataSource dataSource
    ) {
        return builder
            .dataSource(dataSource)
            .packages(
                "com.monsterbank.ms_account.account.command.entity"
            )
            .persistenceUnit("write")
            .build();
    }

    @Bean(name = "writeTransactionManager")
    public PlatformTransactionManager writeTransactionManager(
        @Qualifier("writeEntityManagerFactory")
        EntityManagerFactory entityManagerFactory
    ) {
        return new JpaTransactionManager(entityManagerFactory);
    }
}