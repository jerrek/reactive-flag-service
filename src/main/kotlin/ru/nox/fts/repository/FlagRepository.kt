package ru.nox.fts.repository

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono
import ru.nox.fts.model.Flag

@Repository
interface FlagRepository : ReactiveCrudRepository<Flag, Long>{
    fun findFeatureToggleByName(name: String): Mono<Flag>
}