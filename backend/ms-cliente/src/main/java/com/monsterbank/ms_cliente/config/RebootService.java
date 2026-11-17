package com.monsterbank.ms_cliente.config;

import javax.sql.DataSource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RebootService {
    private final DataSource dataSource;

    public RebootService(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Transactional
    public void recriarDados() {
        JdbcTemplate jdbc = new JdbcTemplate(dataSource);
        jdbc.execute("TRUNCATE TABLE solicitacao_cliente, clientes CASCADE");
        new ResourceDatabasePopulator(new ClassPathResource("db/seed.sql"))
            .execute(dataSource);
    }
}
