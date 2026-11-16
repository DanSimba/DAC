package com.monsterbank.ms_account.config;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Component;

@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true")
public class AccountSeedRunner implements ApplicationRunner {
    private final DataSource writeDataSource;
    private final DataSource readDataSource;

    public AccountSeedRunner(
        @Qualifier("writeDataSource") DataSource writeDataSource,
        @Qualifier("readDataSource") DataSource readDataSource
    ) {
        this.writeDataSource = writeDataSource;
        this.readDataSource = readDataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        executar(writeDataSource, "db/seed.sql");
        executar(readDataSource, "db/seedread.sql");
    }

    private void executar(DataSource dataSource, String arquivo) {
        ResourceDatabasePopulator populator = new ResourceDatabasePopulator(
            new ClassPathResource(arquivo)
        );
        populator.execute(dataSource);
    }
}
