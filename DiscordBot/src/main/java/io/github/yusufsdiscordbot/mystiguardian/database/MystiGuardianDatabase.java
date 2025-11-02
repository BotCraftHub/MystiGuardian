/*
 * Copyright 2025 RealYusufIsmail.
 *
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 *
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */ 
package io.github.yusufsdiscordbot.mystiguardian.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import lombok.val;
import org.flywaydb.core.Flyway;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@Getter
@Slf4j
public class MystiGuardianDatabase {
    private final HikariConfig config;
    private final HikariDataSource ds;

    public MystiGuardianDatabase() {
        val properties = new Properties();

        properties.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        properties.setProperty("dataSource.user", MystiGuardianUtils.getDataSourceConfig().user());
        properties.setProperty(
                "dataSource.password", MystiGuardianUtils.getDataSourceConfig().password());
        properties.setProperty(
                "dataSource.databaseName", MystiGuardianUtils.getDataSourceConfig().name());
        properties.setProperty(
                "dataSource.portNumber", MystiGuardianUtils.getDataSourceConfig().port());
        properties.setProperty(
                "dataSource.serverName", MystiGuardianUtils.getDataSourceConfig().host());
        properties.setProperty("maximumPoolSize", "10");
        properties.setProperty("minimumIdle", "5");
        properties.setProperty("idleTimeout", "30000");
        properties.setProperty("connectionTimeout", "30000");

        config = new HikariConfig(properties);
        ds = new HikariDataSource(config);

        logger.info("Attempting to establish database connection...");
        try (Connection ignored = ds.getConnection()) {
            logger.info("Database connection established successfully.");
            // Run Flyway migrations
            logger.info("Running database migrations...");
            Flyway flyway;
            ClassLoader previousCl = Thread.currentThread().getContextClassLoader();
            ClassLoader candidateCl = MystiGuardianDatabase.class.getClassLoader();
            try {
                // Temporarily set context classloader to this class' classloader so Flyway can discover its
                // providers
                Thread.currentThread().setContextClassLoader(candidateCl);
                try {
                    flyway =
                            Flyway.configure()
                                    .dataSource(ds)
                                    .locations("classpath:db/migration")
                                    .baselineOnMigrate(true)
                                    .load();
                } catch (org.flywaydb.core.api.FlywayException e) {
                    // Some hosting environments or shaded JARs can break Flyway's ServiceLoader discovery
                    // Retry with a more permissive location (no explicit prefix)
                    logger.warn(
                            "Flyway configuration with 'classpath:' failed, retrying without prefix. This often happens in shaded/fat JARs.",
                            e);
                    flyway =
                            Flyway.configure()
                                    .dataSource(ds)
                                    .locations("db/migration")
                                    .baselineOnMigrate(true)
                                    .load();
                }
            } finally {
                // Restore previous context classloader
                Thread.currentThread().setContextClassLoader(previousCl);
            }

            try {
                int migrationsExecuted = flyway.migrate().migrationsExecuted;
                logger.info("Database migrations completed. {} migration(s) executed.", migrationsExecuted);
                logger.info("Database tables initialized successfully.");
            } catch (org.flywaydb.core.api.FlywayException fe) {
                logger.error("Flyway migration failed", fe);
            }
        } catch (SQLException e) {
            logger.error("Failed to initialize database connection", e);
        }
    }

    public @NotNull DSLContext getContext() {
        return DSL.using(ds, SQLDialect.POSTGRES);
    }
}
