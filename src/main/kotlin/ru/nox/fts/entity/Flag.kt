package ru.nox.fts.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table
import java.util.UUID

@Table("flag")
data class Flag(
    @Id val id: UUID?,
    val name: String,
    val enabled: Boolean
)
