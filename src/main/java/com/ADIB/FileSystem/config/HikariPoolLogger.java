package com.ADIB.FileSystem.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

@Component
public class HikariPoolLogger implements ApplicationRunner {

    private static final Logger logger = LoggerFactory.getLogger(HikariPoolLogger.class);

    private final DataSource dataSource;

    public HikariPoolLogger(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (dataSource instanceof HikariDataSource hikariDataSource) {
            logger.info(
                    "HikariCP pool '{}' configured: minimumIdle={}, maximumPoolSize={}, connectionTimeout={}ms, idleTimeout={}ms, maxLifetime={}ms",
                    hikariDataSource.getPoolName(),
                    hikariDataSource.getMinimumIdle(),
                    hikariDataSource.getMaximumPoolSize(),
                    hikariDataSource.getConnectionTimeout(),
                    hikariDataSource.getIdleTimeout(),
                    hikariDataSource.getMaxLifetime()
            );
            return;
        }

        logger.warn("Configured DataSource is '{}', not HikariDataSource", dataSource.getClass().getName());
    }
}
