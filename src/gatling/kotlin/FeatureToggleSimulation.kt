import io.gatling.javaapi.core.CoreDsl.*
import io.gatling.javaapi.core.Simulation
import io.gatling.javaapi.http.HttpDsl.http
import io.gatling.javaapi.http.HttpDsl.status


class FeatureToggleLoadTest : Simulation() {

    // Определение базового URL
    private val httpProtocol = http.baseUrl("http://localhost:8080")

    // HTTP-запрос для получения фича-тогла
    private val getFeatureToggleScenario = scenario("Get Feature Toggle Scenario")
        .repeat(1000,"") { // Повторяем 1000 раз
            exec(
                http("Get Feature Toggle Request") // Название запроса
                    .get("/api/feature-toggle/feature-1") // Эндпоинт
                    .check(status().`is`(200)) // Проверка на 200 OK
            )
        }

    // Конфигурация нагрузки
    init {
        setUp(
            getFeatureToggleScenario.injectOpen(
                atOnceUsers(50), // 50 одновременных пользователей
                rampUsers(100).during(30) // Плавное увеличение до 100 пользователей за 30 секунд
            )
        ).protocols(httpProtocol)
    }
}