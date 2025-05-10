package ru.nox.fts.config

import ru.nox.fts.model.FeatureToggle
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.RedisSerializationContext
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {
    @Bean
    fun reactiveRedisTemplate(connectionFactory: ReactiveRedisConnectionFactory): ReactiveRedisTemplate<String, FeatureToggle> {
        val serializer = Jackson2JsonRedisSerializer(FeatureToggle::class.java)
        val stringSerializer = StringRedisSerializer()
        val redisMapping = RedisSerializationContext.newSerializationContext<String, FeatureToggle>()
            .key(stringSerializer)
            .value(serializer)
            .hashKey(stringSerializer)
            .hashValue(serializer)
            .build()
        return ReactiveRedisTemplate(connectionFactory, redisMapping)
    }
}