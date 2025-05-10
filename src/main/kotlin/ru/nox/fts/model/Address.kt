package ru.nox.fts.model

import java.util.*

data class Address(
    val id: String = UUID.randomUUID().toString(),
    val street: Street,
    val house: String
)

data class Street(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val city: City
)

data class City(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val country: Country
)

data class Country(
    val id: String = UUID.randomUUID().toString(),
    val name: String
)