package com.proyectovegeta.seminario.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.jdbc.DataSourceBuilder;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;
import java.util.Objects;

@Configuration
public class DatabaseConfig {
    @Bean
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource psqlDataSource() {
        return DataSourceBuilder.create().build();
    }

    @Primary
    @Bean(name = "entityManagerFactory")
    public LocalContainerEntityManagerFactoryBean psqlEntityManagerFactory(
            EntityManagerFactoryBuilder builder,
            @Qualifier("psqlDataSource") DataSource dataSource
    ) {
        return builder
                .dataSource(dataSource)
                .packages("com.proyectovegeta.seminario.model")
                .persistenceUnit("psql")
                .build();
    }

    @Bean(name = "transactionManager")
    public PlatformTransactionManager psqlTransactionManager(
            @Qualifier("entityManagerFactory") LocalContainerEntityManagerFactoryBean psqlEntityManagerFactory
    ) {
        return new JpaTransactionManager(Objects.requireNonNull(psqlEntityManagerFactory.getObject()));
    }
}
