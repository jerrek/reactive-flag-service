package ru.nox.fts.service

import ru.nox.fts.repository.FeatureToggleRepository
import ru.nox.fts.model.FeatureToggle
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono

@Service
class FeatureToggleService(
    private val repository: FeatureToggleRepository,
    private val redisTemplate: ReactiveRedisTemplate<String, FeatureToggle>
) {

    // Получение фича тогла
    fun getFeatureToggle(name: String): Mono<FeatureToggle> {
        return redisTemplate.opsForValue().get(name)
            .switchIfEmpty(
                repository.findByName(name)
                    .flatMap { toggle ->
                        redisTemplate.opsForValue().set(name, toggle).thenReturn(toggle)
                    }
            )
    }

    // Сохранение фича тогла
    fun saveFeatureToggle(featureToggle: FeatureToggle): Mono<FeatureToggle> {
        return repository.saveFeatureToggle(featureToggle)
            .flatMap { id ->
                val savedToggle = featureToggle.copy(id = id)
                // Обновление кэша после сохранения
                redisTemplate.opsForValue()
                    .set(savedToggle.name, savedToggle)
                    .thenReturn(savedToggle)
            }
    }
}