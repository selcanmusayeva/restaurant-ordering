package com.ordering.ordering.config;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@EntityScan(basePackages = {
    "com.ordering.ordering.model",
    "com.ordering.restaurant.model"
})
public class EntityScanConfig {
} 