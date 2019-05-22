package xyz.pavelkorolev.githubrepos.services

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.network.models.ServerRepository
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object ServerRepositoryMapperSpec : Spek({
    describe("Mapper") {
        val mapper by memoized { ServerRepositoryMapper() }
        it("should map from server model") {
            val serverRepository = ServerRepository(
                1,
                "spek",
                "https://github.com/spekframework/spek",
                "A specification framework for Kotlin"
            )
            val expectedRepository = Repository(
                1,
                "spek",
                "A specification framework for Kotlin",
                "https://github.com/spekframework/spek"
            )
            val actualRepository = mapper.map(serverRepository)
            assertEquals(expectedRepository, actualRepository)
        }
        it("should map list from server model") {
            val serverRepository1 = ServerRepository(
                1,
                "spek",
                "https://github.com/spekframework/spek",
                "A specification framework for Kotlin"
            )
            val serverRepository2 = ServerRepository(
                2,
                "kotlin",
                "https://github.com/JetBrains/kotlin",
                "The Kotlin Programming Language"
            )
            val expectedRepository1 = Repository(
                1,
                "spek",
                "A specification framework for Kotlin",
                "https://github.com/spekframework/spek"
            )
            val expectedRepository2 = Repository(
                2,
                "kotlin",
                "The Kotlin Programming Language",
                "https://github.com/JetBrains/kotlin"
            )
            val serverRepositories = listOf(serverRepository1, serverRepository2)
            val expectedRepositories = listOf(expectedRepository1, expectedRepository2)
            val actualRepositories = mapper.map(serverRepositories)
            assertEquals(expectedRepositories, actualRepositories)
        }
        it("should throw MappingException when server model id is null") {
            val serverRepository = ServerRepository(
                null,
                "spek",
                "https://github.com/spekframework/spek",
                "A specification framework for Kotlin"
            )
            assertFailsWith(MappingException::class) {
                mapper.map(serverRepository)
            }
        }
        it("should throw MappingException when server model title is null") {
            val serverRepository = ServerRepository(
                1,
                null,
                "https://github.com/spekframework/spek",
                "A specification framework for Kotlin"
            )
            assertFailsWith(MappingException::class) {
                mapper.map(serverRepository)
            }
        }
        it("should throw MappingException when server model url is null") {
            val serverRepository = ServerRepository(
                1,
                "spek",
                null,
                "A specification framework for Kotlin"
            )
            assertFailsWith(MappingException::class) {
                mapper.map(serverRepository)
            }
        }
        it("reverse mapping should throw UnsupportedOperationException") {
            val repository = Repository(
                1,
                "spek",
                "A specification framework for Kotlin",
                "https://github.com/spekframework/spek"
            )
            assertFailsWith(UnsupportedOperationException::class) {
                mapper.reverseMap(repository)
            }
        }
    }
})