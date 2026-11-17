package com.monsterbank.ms_gerente.config;

import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class RebootService {
    private final DataSource dataSource;
    private final JdbcTemplate jdbcTemplate;

    public RebootService(DataSource dataSource) {
        this.dataSource = dataSource;
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public void recriarDados() {
        jdbcTemplate.execute("TRUNCATE TABLE gerente RESTART IDENTITY CASCADE");
        new ResourceDatabasePopulator(new ClassPathResource("db/dataMockup.sql"))
            .execute(dataSource);
    }
}
