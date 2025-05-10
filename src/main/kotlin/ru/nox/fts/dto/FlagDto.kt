package ru.nox.fts.ru.nox.fts.dto

import java.util.*

data class FlagDto(
    val id: UUID?,
    val name: String,
    val enabled: Boolean
)
