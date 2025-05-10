import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status
import java.time.Duration
import java.util.UUID

class FlagFindAllSimulation : Simulation() {

    private val baseUrl = "http://localhost:8080/api"

    private val httpProtocol = http.baseUrl(baseUrl)
        .acceptHeader("application/json")
        .contentTypeHeader("application/json")

    private val scn = scenario("Feature Toggle Load Test")
        .exec(
            http("Get All Feature Toggles")
                .get("/feature-toggles")
                .check(status().`is`(200))
        )
        .pause(Duration.ofMillis(1000)) // Имитация времени ожидания пользователя
        .exec(
            http("Create New Feature Toggle")
                .post("/feature-toggles")
                .body(
                    StringBody{ session ->
                        """
                        {
                            "name": "test${UUID.randomUUID()}",
                            "enabled": "true"                        
                        }
                        """}
                ).asJson()
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