package ru.nox.fts.config

import io.r2dbc.spi.ConnectionFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.r2dbc.core.DatabaseClient

@Configuration
class R2DBCConfig {
    @Bean
    fun databaseClient(connectionFactory: ConnectionFactory): DatabaseClient =
        DatabaseClient.create(connectionFactory)
}