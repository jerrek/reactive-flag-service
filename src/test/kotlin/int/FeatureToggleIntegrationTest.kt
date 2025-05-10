//package int
//
//import org.junit.jupiter.api.Test
//import org.springframework.beans.factory.annotation.Autowired
//import org.springframework.boot.test.context.SpringBootTest
//import org.springframework.http.MediaType
//import org.springframework.test.web.reactive.server.WebTestClient
//import org.testcontainers.containers.PostgreSQLContainer
//import org.testcontainers.junit.jupiter.Container
//import org.testcontainers.junit.jupiter.Testcontainers
//import ru.nox.fts.model.FeatureToggle
//import java.util.UUID
//
//@SpringBootTest
//@Testcontainers
//class FeatureToggleIntegrationTest {
//
//    @Container
//    val postgreSQLContainer = PostgreSQLContainer("postgres:latest")
//
//    @Autowired
//    lateinit var webTestClient: WebTestClient
//
//    @Test
//    fun `test save feature toggle`() {
//        val newFeatureToggle = FeatureToggle(
//            name = "new-feature",
//            enabled = true,
//            addressId = UUID.randomUUID().toString()
//            )
//
//        webTestClient.post().uri("/api/feature-toggle")
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(newFeatureToggle)
//            .exchange()
//            .expectStatus().isOk
//            .expectBody()
//            .jsonPath("$.name").isEqualTo("new-feature")
//            .jsonPath("$.enabled").isEqualTo(true)
//    }
//}