package unit

import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.data.redis.core.ReactiveListOperations
import org.springframework.data.redis.core.ReactiveRedisTemplate
import org.springframework.test.context.junit.jupiter.SpringExtension
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import ru.nox.fts.entity.Flag
import ru.nox.fts.repository.FlagRepository
import ru.nox.fts.service.FlagService
import java.util.*

@ExtendWith(SpringExtension::class)
class FlagServiceTest {

    private lateinit var repository: FlagRepository
    private lateinit var redisTemplate: ReactiveRedisTemplate<String, Any>
    private lateinit var listOperations: ReactiveListOperations<String, Any>
    private lateinit var service: FlagService

    @BeforeEach
    fun setUp() {
        repository = mockk()
        redisTemplate = mockk()
        listOperations = mockk()

        every { redisTemplate.opsForList() } returns listOperations

        service = FlagService(repository, redisTemplate)
    }

    @Test
    fun `should return all toggles from cache when available`() {
        // Arrange
        val toggles = listOf(
            Flag(UUID.randomUUID(),"toggle1", true),
            Flag(UUID.randomUUID(),"toggle2", false)
        )
        every { listOperations.range("toggles", 0, -1) } returns Flux.fromIterable(toggles)
        every { repository.findAll() } returns Flux.fromIterable(toggles)
        every { listOperations.rightPush(any(), any()) } returns Mono.just(1)

        // Act
        val result = service.getAllToggles()

        // Assert
        StepVerifier.create(result)
            .expectNext(toggles[0])
            .expectNext(toggles[1])
            .verifyComplete()

        verify { listOperations.range("toggles", 0, -1) }
        confirmVerified(listOperations)
    }

    @Test
    fun `should fetch toggles from repository and cache when cache is empty`() {
        // Arrange
        val toggles = listOf(
            Flag(UUID.randomUUID(),"toggle1", true),
            Flag(UUID.randomUUID(),"toggle2", false)
        )
        every { listOperations.range("toggles", 0, -1) } returns Flux.empty()
        every { repository.findAll() } returns Flux.fromIterable(toggles)
        every { listOperations.rightPush(any(), any()) } returns Mono.just(1)

        // Act
        val result = service.getAllToggles()

        // Assert
        StepVerifier.create(result)
            .expectNext(toggles[0])
            .expectNext(toggles[1])
            .verifyComplete()

        verify { listOperations.range("toggles", 0, -1) }
        verify { repository.findAll() }
        toggles.forEach {
            verify { listOperations.rightPush("toggles", it) }
        }
        confirmVerified(repository, listOperations)
    }

    @Test
    fun `should create toggle and add it to cache`() {
        // Arrange
        val toggle = Flag(UUID.randomUUID(),"toggle1", true)
        every { repository.save(toggle) } returns Mono.just(toggle)
        every { listOperations.rightPush("toggles", toggle) } returns Mono.just(1)

        // Act
        val result = service.createToggle(toggle)

        // Assert
        StepVerifier.create(result)
            .expectNext(toggle)
            .verifyComplete()

        verify { repository.save(toggle) }
        verify { listOperations.rightPush("toggles", toggle) }
        confirmVerified(repository, listOperations)
    }
}