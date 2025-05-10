package int

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.boot.web.embedded.netty.NettyReactiveWebServerFactory
import org.springframework.boot.web.reactive.server.ReactiveWebServerFactory
import org.springframework.boot.web.server.ConfigurableWebServerFactory
import org.springframework.boot.web.server.WebServerFactoryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.http.server.reactive.HttpHandler
import org.springframework.web.reactive.function.server.RequestPredicates.GET
import org.springframework.web.reactive.function.server.RequestPredicates.POST
import org.springframework.web.reactive.function.server.RouterFunction
import org.springframework.web.reactive.function.server.RouterFunctions
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.testcontainers.containers.PostgreSQLContainer
import org.testcontainers.containers.GenericContainer
//TODO исправить тесты
@TestConfiguration
class TestContainerConfig {

    companion object {
        val postgresContainer = PostgreSQLContainer("postgres:15.2").apply {
            withDatabaseName("testdb")
            withUsername("testuser")
            withPassword("testpass")
            start()
        }

        val redisContainer = GenericContainer<Nothing>("redis:7.0.5").apply {
            withExposedPorts(6379)
            start()
        }
    }

    @Bean
    fun postgresContainer(): PostgreSQLContainer<*> = postgresContainer

    @Bean
    fun redisContainer(): GenericContainer<Nothing> = redisContainer

    /**
     * Создает бин ReactiveWebServerFactory с настройками по умолчанию
     * и возможностью кастомизации.
     */
    @Bean
    fun reactiveWebServerFactory(): ReactiveWebServerFactory {
        return NettyReactiveWebServerFactory().apply {
            // Кастомизация сервера, например, установка порта
            setPort(8080)
        }
    }

    /**
     * Дополнительная настройка WebServerFactoryCustomizer, которая позволяет
     * переопределять параметры сервера.
     */
    @Bean
    fun webServerFactoryCustomizer(): WebServerFactoryCustomizer<ConfigurableWebServerFactory> {
        return WebServerFactoryCustomizer { factory ->
            factory.setPort(9090) // Изменяем порт
            factory.setAddress(java.net.InetAddress.getByName("127.0.0.1")) // Ограничиваем на localhost
        }
    }

    @Bean
    fun httpHandler(): HttpHandler {
        return RouterFunctions.toHttpHandler(route())
    }

    @Bean
    fun route(): RouterFunction<ServerResponse> {
        return RouterFunctions
            .route(GET("/hello")) { ServerResponse.ok().bodyValue("Hello, world!") }
            .andRoute(POST("/greet")) { req: ServerRequest ->
                req.bodyToMono(String::class.java).flatMap { name ->
                    ServerResponse.ok().bodyValue("Hello, $name!")
                }
            }
    }
}