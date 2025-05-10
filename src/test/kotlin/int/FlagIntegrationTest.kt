package int

import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.junit.jupiter.Testcontainers
//TODO исправить тесты
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [FlagIntegrationTest::class])
@ContextConfiguration(classes = [TestContainerConfig::class])
@Testcontainers
class FlagIntegrationTest {

    @Autowired
    lateinit var webTestClient: WebTestClient

    companion object {
        @BeforeAll
        @JvmStatic
        fun setup() {
            System.setProperty(
                "spring.r2dbc.url",
                "r2dbc:postgresql://${TestContainerConfig.postgresContainer.host}:${TestContainerConfig.postgresContainer.firstMappedPort}/testdb"
            )
            System.setProperty("spring.redis.host", TestContainerConfig.redisContainer.host)
            System.setProperty(
                "spring.redis.port",
                TestContainerConfig.redisContainer.firstMappedPort.toString()
            )
        }
    }

    @Test
    fun `should get empty list of feature toggles`() {
        webTestClient.get()
            .uri("/api/feature-toggles")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .json("[]")
    }

    @Test
    fun `should create and retrieve a feature toggle`() {
        val featureToggle = mapOf(
            "name" to "new_feature",
            "enabled" to true,
            "country" to "CountryX",
            "city" to "CityY",
            "street" to "StreetZ",
            "house" to "123"
        )

        // Создание фича тоггла
        webTestClient.post()
            .uri("/api/feature-toggles")
            .bodyValue(featureToggle)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("new_feature")
            .jsonPath("$.enabled").isEqualTo(true)

        // Получение всех фича тогглов
        webTestClient.get()
            .uri("/api/feature-toggles")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$[0].name").isEqualTo("new_feature")
            .jsonPath("$[0].enabled").isEqualTo(true)
    }
}