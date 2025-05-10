package int

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.DynamicPropertyRegistry
import org.springframework.test.context.DynamicPropertySource
import org.springframework.test.web.reactive.server.WebTestClient
import org.testcontainers.containers.GenericContainer
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import ru.nox.fts.FlagServiceApp
import ru.nox.fts.entity.Flag
import ru.nox.fts.repository.FlagRepository
import java.util.*
//TODO исправить тесты
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT, classes = [FlagControllerIntegrationTest::class, FlagServiceApp::class])
@AutoConfigureWebTestClient
@ContextConfiguration(classes = [TestContainerConfig::class])
@Testcontainers
class FlagControllerIntegrationTest {

    companion object {
        @Container
        val postgresContainer = PostgreSQLContainer("postgres:15-alpine").apply {
            withDatabaseName("testdb")
            withUsername("test")
            withPassword("test")
        }

        @Container
        val redisContainer = GenericContainer("redis:6-alpine").apply {
            withExposedPorts(6379)
        }

        @JvmStatic
        @DynamicPropertySource
        fun registerDynamicProperties(registry: DynamicPropertyRegistry) {
            registry.add("spring.r2dbc.url") {
                "r2dbc:postgresql://${postgresContainer.host}:${postgresContainer.firstMappedPort}/testdb"
            }
            registry.add("spring.r2dbc.username", postgresContainer::getUsername)
            registry.add("spring.r2dbc.password", postgresContainer::getPassword)

            registry.add("spring.redis.host", redisContainer::getHost)
            registry.add("spring.redis.port") { redisContainer.firstMappedPort }
        }
    }

    @Autowired
    lateinit var webTestClient: WebTestClient

    @Autowired
    lateinit var repository: FlagRepository

    @BeforeEach
    fun setUp() {
        repository.deleteAll().block()
    }

    @Test
    fun `should create a new flags`() {
        val flag = Flag(
            id = UUID.randomUUID(),
            name = "new-feature",
            enabled = true
        )

        webTestClient.post()
            .uri("/api/flag")
            .bodyValue(flag)
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("$.name").isEqualTo("new-feature")
            .jsonPath("$.enabled").isEqualTo(true)
    }

    @Test
    fun `should retrieve all flags`() {
        val toggles = listOf(
            Flag(UUID.randomUUID(),"feature1", true),
            Flag(UUID.randomUUID(),"feature2", false)
        )

        repository.saveAll(toggles).blockLast()

        webTestClient.get()
            .uri("/api/feature-toggles")
            .exchange()
            .expectStatus().isOk
            .expectBodyList(Flag::class.java)
            .hasSize(2)
    }
}