package io.github.yusufsdiscordbot.mystiguardian.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.realyusufismail.jconfig.util.JConfigUtils;
import lombok.Getter;
import lombok.val;
import org.jetbrains.annotations.NotNull;
import org.jooq.DSLContext;
import org.jooq.SQLDialect;
import org.jooq.impl.DSL;

import java.sql.SQLException;
import java.util.Properties;

import static io.github.yusufsdiscordbot.mystiguardian.database.HandleDataBaseTables.addTablesToDatabase;
import static io.github.yusufsdiscordbot.mystiguardian.utils.MystiGuardianUtils.databaseLogger;

@Getter
public class MystiGuardianDatabase {
    private final HikariConfig config;
    private final HikariDataSource ds;

    public MystiGuardianDatabase() {
        val properties = new Properties();
        properties.setProperty("dataSourceClassName", "org.postgresql.ds.PGSimpleDataSource");
        properties.setProperty("dataSource.user", JConfigUtils.getString("database.user"));
        properties.setProperty("dataSource.password", JConfigUtils.getString("database.password"));
        properties.setProperty("dataSource.databaseName", JConfigUtils.getString("database.name"));
        properties.setProperty("dataSource.portNumber", JConfigUtils.getString("database.port"));
        properties.setProperty("dataSource.serverName", JConfigUtils.getString("database.host"));
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
