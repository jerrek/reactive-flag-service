package ru.nox.fts.service

import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.DependsOn
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.stereotype.Service
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.core.scheduler.Schedulers
import ru.nox.fts.entity.Flag
import ru.nox.fts.repository.FlagRepository

@Service
@DependsOn("redisConfig")
class FlagService(
    private val repository: FlagRepository,
    private val customReactiveRedisTemplate: ReactiveRedisTemplate<String, Any>
) {

    @PostConstruct
    fun init() {
        customReactiveRedisTemplate.delete("flags").block()
    }

    fun getFeatureToggleByName(name: String): Mono<Any> =
        customReactiveRedisTemplate.opsForValue().get("flag:$name")
            .switchIfEmpty(
                repository.findFeatureToggleByName(name).publishOn(Schedulers.boundedElastic()).doOnNext { toggle ->
                    customReactiveRedisTemplate.opsForValue()
                        .set("flag:$name", toggle)
                        .subscribe()
                }
            )

    fun getAllToggles(): Flux<Any> =
        customReactiveRedisTemplate.opsForList().range("flag", 0, -1)
            .switchIfEmpty(repository.findAll().publishOn(Schedulers.boundedElastic()).doOnNext {
                customReactiveRedisTemplate.opsForList().rightPush("flag", it).subscribe()
            })

    fun createToggle(toggle: Flag): Mono<Flag> =
        repository.save(toggle).publishOn(Schedulers.boundedElastic()).doOnSuccess {
            customReactiveRedisTemplate.opsForList().rightPush("flag", it).subscribe()
        }
}