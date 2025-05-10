package ru.nox.fts.model

import java.util.UUID

data class FeatureToggle(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val enabled: Boolean,
    val addressId: String?
)