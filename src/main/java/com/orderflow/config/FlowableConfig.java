package com.orderflow.config;

import org.flowable.spring.SpringProcessEngineConfiguration;
import org.flowable.spring.boot.EngineConfigurationConfigurer;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

/**
 * Configuration for Flowable BPMN engine.
 * Configures Flowable to use the flowableDataSource with flowable_schema.
 */
@Configuration
public class FlowableConfig {

    /**
     * Configure Flowable engine to use the dedicated flowable datasource.
     */
    @Bean
    public EngineConfigurationConfigurer<SpringProcessEngineConfiguration> flowableProcessEngineConfigurer(
            @Qualifier("flowableDataSource") DataSource flowableDataSource) {
        return engineConfiguration -> {
            engineConfiguration.setDataSource(flowableDataSource);
            engineConfiguration.setDatabaseSchemaUpdate("true");
            engineConfiguration.setDatabaseSchema("flowable_schema");
            engineConfiguration.setAsyncExecutorActivate(true);
        };
    }
}
