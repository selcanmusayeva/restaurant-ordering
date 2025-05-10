package com.ordering.ordering.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableJpaRepositories(basePackages = {
    "com.ordering.ordering.repository",
    "com.ordering.restaurant.repository"
})
@EnableTransactionManagement
public class JpaConfig {
} 