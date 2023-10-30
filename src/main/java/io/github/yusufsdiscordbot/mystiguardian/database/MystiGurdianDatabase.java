package io.github.yusufsdiscordbot.mystiguardian.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.github.realyusufismail.jconfig.util.JConfigUtils;
import lombok.val;

import java.util.Properties;

import static io.github.yusufsdiscordbot.mystigurdian.utils.MystiGurdianUtils.databaseLogger;

public class MystiGurdianDatabase {
    private HikariConfig config = new HikariConfig();
    private final HikariDataSource ds;

    public MystiGurdianDatabase() {
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
    }
}
