package ru.nox.fts.controller

import ru.nox.fts.model.FeatureToggle
import ru.nox.fts.service.FeatureToggleService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/feature-toggle")
class FeatureToggleController(private val service: FeatureToggleService) {

    // Получение фича тогла
    @GetMapping("/{name}")
    fun getFeatureToggle(@PathVariable name: String): Mono<ResponseEntity<FeatureToggle>> {
        return service.getFeatureToggle(name)
            .map { ResponseEntity.ok(it) }
            .defaultIfEmpty(ResponseEntity.notFound().build())
    }

    // Сохранение фича тогла
    @PostMapping
    fun saveFeatureToggle(@RequestBody featureToggle: FeatureToggle): Mono<ResponseEntity<FeatureToggle>> {
        return service.saveFeatureToggle(featureToggle)
            .map { ResponseEntity.ok(it) }
            .onErrorResume {
                println("FUCK")
                Mono.just(ResponseEntity.badRequest().build())
            }
    }
}