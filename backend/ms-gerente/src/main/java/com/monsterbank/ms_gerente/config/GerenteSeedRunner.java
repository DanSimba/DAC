package com.monsterbank.ms_gerente.config;

import javax.sql.DataSource;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class GerenteSeedRunner implements ApplicationRunner {
    private final DataSource dataSource;
    private final RebootService rebootService;

    public GerenteSeedRunner(DataSource dataSource, RebootService rebootService) {
        this.dataSource = dataSource;
        this.rebootService = rebootService;
    }

    @Override
    public void run(ApplicationArguments args) {
        Long total = new JdbcTemplate(dataSource).queryForObject(
            "SELECT COUNT(*) FROM gerente",
            Long.class
        );

        if (total == null || total == 0) {
            rebootService.recriarDados();
        }
    }
}
