/*
 * Copyright 2023 RealYusufIsmail.
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

import static io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables.addTablesToDatabase;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.databaseLogger;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.jConfig;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.SQLException;
import java.util.Properties;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

@Getter
public class MystiGuardianDatabase {
    private final HikariConfig config;
    private final HikariDataSource ds;

    public MystiGuardianDatabase() {
        val properties = new Properties();
        val dataSource = jConfig.get("dataSource");

        if (dataSource == null) {
            databaseLogger.error("No dataSource found in config");
            throw new RuntimeException("No dataSource found in config");
        }

        properties.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        properties.setProperty("dataSource.user", dataSource.get("user").asText());
        properties.setProperty("dataSource.password", dataSource.get("password").asText());
        properties.setProperty("dataSource.databaseName", dataSource.get("name").asText());
        properties.setProperty("dataSource.portNumber", dataSource.get("port").asText());
        properties.setProperty("dataSource.serverName", dataSource.get("host").asText());
        properties.setProperty("maximumPoolSize", "10");
        properties.setProperty("minimumIdle", "5");
        properties.setProperty("idleTimeout", "30000");
        properties.setProperty("connectionTimeout", "30000");

        config = new HikariConfig(properties);
        ds = new HikariDataSource(config);

        databaseLogger.info("Database connection established");

        try {
            addTablesToDatabase(ds.getConnection());
        } catch (SQLException e) {
            databaseLogger.error("Error while adding tables to database", e);
        }
    }

    public @NotNull DSLContext getContext() {
        return DSL.using(ds, SQLDialect.POSTGRES);
    }
}
