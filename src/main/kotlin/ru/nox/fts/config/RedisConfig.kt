package ru.nox.fts.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.repository.configuration.EnableRedisRepositories
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
@EnableRedisRepositories
class RedisConfig {

    /**
     * Конфигурация ReactiveRedisConnectionFactory для подключения к Redis.
     */
    @Primary
    @Bean
    fun reactiveRedisConnectionFactory(): ReactiveRedisConnectionFactory {
        return LettuceConnectionFactory("localhost", 6379) // Адрес и порт Redis
    }

    /**
     * Настройка ReactiveRedisTemplate с использованием JSON сериализации для значений.
     */
    @Bean
    fun customReactiveRedisTemplate(
        connectionFactory: ReactiveRedisConnectionFactory,
        objectMapper: ObjectMapper
    ): ReactiveRedisTemplate<String, Any> {

        // Сериализатор для ключей
        val keySerializer = StringRedisSerializer()

        // Сериализатор для значений (Jackson JSON)
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, Any::class.java)

        // Создание контекста сериализации
        val serializationContext = RedisSerializationContext
            .newSerializationContext<String, Any>(keySerializer)
            .value(valueSerializer)
            .hashKey(keySerializer)
            .hashValue(valueSerializer)
            .build()

        return ReactiveRedisTemplate(connectionFactory, serializationContext)
    }

    /**
     * Бин для ObjectMapper с поддержкой Kotlin.
     */
    @Bean
    fun objectMapper(): ObjectMapper {
        return jacksonObjectMapper().registerKotlinModule()
    }

}