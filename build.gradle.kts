plugins {
    kotlin("jvm") version "2.0.20"
    id("org.springframework.boot") version "3.2.0"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("plugin.spring") version "2.0.20"
    kotlin("plugin.jpa") version "2.0.20"
    id("io.gatling.gradle") version "3.9.5"
}

group = "ru.nox.fts"
version = "1.0.0"

java.sourceCompatibility = JavaVersion.VERSION_21

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-r2dbc")
    implementation("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.springframework.boot:spring-boot-starter-data-redis-reactive")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("io.lettuce:lettuce-core:6.5.1.RELEASE")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("io.projectreactor.kotlin:reactor-kotlin-extensions")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    runtimeOnly("org.postgresql:r2dbc-postgresql")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.liquibase:liquibase-core")
    implementation("org.postgresql:postgresql:42.7.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.9.8")
    implementation("io.netty:netty-codec-http:4.1.115.Final")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("io.projectreactor:reactor-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("com.redis.testcontainers:testcontainers-redis:1.6.4")
    testImplementation("io.mockk:mockk:1.13.7")

    // Зависимость для Gatling
    implementation("io.gatling:gatling-core:3.9.5")
    implementation("io.gatling:gatling-http:3.9.5")
    implementation("io.gatling:gatling-jdbc:3.9.5") // Если требуется JDBC для тестов
    implementation("io.gatling.highcharts:gatling-charts-highcharts:3.9.5") // Для отчетов с Highcharts

    // Зависимость для Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
tasks.jar {
    manifest {
        attributes(
            "Main-Class" to "ru.nox.fts.Fts" // Замените на ваш основной класс
        )
    }
}
//tasks.withType<Jar>() {
//    manifest {
//        attributes["Main-Class"] = "Fts.kt"
//    }
//    configurations["compileClasspath"].forEach { file: File ->
//        from(zipTree(file.absoluteFile))
//    }
//}
kotlin {
    jvmToolchain(21)
}