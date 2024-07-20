package io.cylonsam.cajucreditcardauthorizer;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.testcontainers.service.connection.ServiceConnection;
import org.springframework.context.annotation.Bean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.utility.DockerImageName;

import javax.sql.DataSource;

@TestConfiguration(proxyBeanMethods = false)
class TestcontainersConfiguration {

    @Bean
    @ServiceConnection
    PostgreSQLContainer<?> postgresContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(DockerImageName.parse("postgres:latest"));
        container.start();
        initializeDatabase(container);
        return container;
    }

    private void initializeDatabase(PostgreSQLContainer<?> container) {
        DataSource dataSource = new DriverManagerDataSource(
                container.getJdbcUrl(),
                container.getUsername(),
                container.getPassword()
        );

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        jdbcTemplate.execute("CREATE SCHEMA IF NOT EXISTS caju_credit_card_authorizer;");
        jdbcTemplate.execute("""
            CREATE TABLE IF NOT EXISTS caju_credit_card_authorizer.accounts (
                id UUID PRIMARY KEY,
                food_balance DOUBLE PRECISION,
                cash_balance DOUBLE PRECISION,
                meal_balance DOUBLE PRECISION
            );
        """);
    }
}
