package com.istratenkov.energyregistration.repository;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Testcontainers infrastructural class. Need to start postgres container
 * in integration tests and stop it when tests are done.
 * Also has a functionality to execute natives query to generate test data if needed.
 */
public abstract class AbstractRepositoryTest {

    protected static PostgreSQLContainer sqlContainer;

    static {
        startPostgresContainer();
    }

    public static void startPostgresContainer() {
        sqlContainer = new PostgreSQLContainer("postgres:12")
                .withDatabaseName("test-db")
                .withUsername("postgres")
                .withPassword("password");
        sqlContainer.start();
    }

    public static class Initializer implements
            ApplicationContextInitializer<ConfigurableApplicationContext> {

        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "spring.datasource.url=" + sqlContainer.getJdbcUrl(),
                    "spring.datasource.username=" + sqlContainer.getUsername(),
                    "spring.datasource.password=" + sqlContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    protected ResultSet performQuery(String sql, HikariDataSource dataSource) throws SQLException {
        Connection connection = dataSource.getConnection();
        Statement statement = connection.createStatement();
        statement.execute(sql);
        ResultSet resultSet = statement.getResultSet();
        connection.close();
        if (resultSet != null) {
            resultSet.next();
        }
        return resultSet;
    }
}
