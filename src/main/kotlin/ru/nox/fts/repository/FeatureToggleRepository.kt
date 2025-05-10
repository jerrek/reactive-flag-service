package ru.nox.fts.repository

import ru.nox.fts.model.FeatureToggle
import org.springframework.r2dbc.core.DatabaseClient

import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
class FeatureToggleRepository(private val client: DatabaseClient) {
    fun findByName(name: String): Mono<FeatureToggle> {
        return client.sql(
            """
            SELECT ft.id, ft.name, ft.enabled, a.id AS address_id, a.house, 
                   s.id AS street_id, s.name AS street_name,
                   c.id AS city_id, c.name AS city_name,
                   co.id AS country_id, co.name AS country_name
            FROM feature_toggle ft
            JOIN address a ON ft.address_id = a.id
            JOIN street s ON a.street_id = s.id
            JOIN city c ON s.city_id = c.id
            JOIN country co ON c.country_id = co.id
            WHERE ft.name = :name
            """
        )
            .bind("name", name)
            .map { row -> FeatureToggle(
                name = row["name"] as String,
                enabled = row["enabled"] as Boolean,
                addressId = row["addressId"] as String,
            ) }
            .one()
    }

    fun saveFeatureToggle(featureToggle: FeatureToggle): Mono<String> {
        println("save")
        return client.sql(
            """
            INSERT INTO feature_toggle (name, enabled, address_id)
            VALUES (:name, :enabled, :address_id)
            RETURNING id
            """
        )
            .bind("name", featureToggle.name)
            .bind("enabled", featureToggle.enabled)
            .bind("address_id", featureToggle.addressId)
            .map { row -> row["name"] as String }
            .one()
    }
}