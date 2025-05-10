import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration
import java.util.*

class FlagFindByNameSimulation : Simulation() {

    private val baseUrl = "http://localhost:8080/api"
    private val httpProtocol = http.baseUrl(baseUrl)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")

    private val scn = scenario("Feature Toggle Load Test")
        .exec { session ->
            session.set("uuid", UUID.randomUUID().toString()) // Генерация нового UUID для каждой итерации
        }
        .exec(
            http("Create New Feature Toggle")
                .post("/feature-toggles")
                .body(
                    StringBody { session ->
                        """
                        {
                            "name": "test${session.get<String>("uuid")}",
                            "enabled": "true"
                        }
                        """
                    }
                ).asJson()
                .check(status().`is`(200))
        )
        .pause(Duration.ofMillis(1000)) // Имитация времени ожидания пользователя
        .exec(
            http("Get Feature Toggle by ID")
                .get { session -> "/feature-toggles/${session.get<String>("uuid")}" }
                .check(status().`is`(200))
        )

    init {
        setUp(
            scn.injectOpen(
                atOnceUsers(500),
                rampUsers(2000).during(180)
            )
        ).protocols(httpProtocol)
    }
}