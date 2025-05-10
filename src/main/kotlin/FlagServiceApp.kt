package ru.nox.fts

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class FlagServiceApp

fun main(args: Array<String>) {
    runApplication<FlagServiceApp>(*args)
}