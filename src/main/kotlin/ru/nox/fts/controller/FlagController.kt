package ru.nox.fts.controller

import jakarta.validation.Valid
import ru.nox.fts.entity.Flag
import ru.nox.fts.service.FlagService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/api/flags")
class FlagController(private val service: FlagService) {

    @GetMapping("/{name}")
    fun getFeatureToggle(@PathVariable name: String): Mono<Any> {
        return service.getFeatureToggleByName(name)
    }

    @GetMapping
    fun getAll(): Flux<Any> = service.getAllToggles()

    @PostMapping
    fun create(@RequestBody @Valid toggle: Flag): Mono<ResponseEntity<Flag>> =
        service.createToggle(toggle)
            .map { ResponseEntity.ok(it) }
}