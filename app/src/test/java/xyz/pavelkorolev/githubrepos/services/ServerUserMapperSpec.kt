package xyz.pavelkorolev.githubrepos.services;

import org.spekframework.spek2.Spek
import org.spekframework.spek2.style.specification.describe
import xyz.pavelkorolev.githubrepos.models.User
import xyz.pavelkorolev.githubrepos.network.models.ServerUser
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith

object ServerUserMapperSpec : Spek({
    describe("Mapper") {
        val mapper by memoized { ServerUserMapper() }
        it("should map from server model") {
            val serverUser = ServerUser(
                1,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4",
                "https://github.com/PavelKorolev",
                "pk@example.com"
            )
            val expectedUser = User(
                1,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4"
            )
            val actualUser = mapper.map(serverUser)
            assertEquals(expectedUser, actualUser)
        }
        it("should map list from server model") {
            val serverUser1 = ServerUser(
                1,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4",
                "https://github.com/PavelKorolev",
                "pk@example.com"
            )
            val serverUser2 = ServerUser(
                2,
                "JakeWharton",
                "https://avatars1.githubusercontent.com/u/66577?s=460&v=4",
                "https://github.com/JakeWharton",
                "jw@example.com"
            )
            val expectedUser1 = User(
                1,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4"
            )
            val expectedUser2 = User(
                2,
                "JakeWharton",
                "https://avatars1.githubusercontent.com/u/66577?s=460&v=4"
            )
            val serverUsers = listOf(serverUser1, serverUser2)
            val expectedUsers = listOf(expectedUser1, expectedUser2)
            val actualUsers = mapper.map(serverUsers)
            assertEquals(expectedUsers, actualUsers)
        }
        it("should throw MappingException when server model id is null") {
            val serverUser = ServerUser(
                null,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4",
                "https://github.com/PavelKorolev",
                "pk@example.com"
            )
            assertFailsWith(MappingException::class) {
                mapper.map(serverUser)
            }
        }
        it("should throw MappingException when server model login is null") {
            val serverUser = ServerUser(
                1,
                null,
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4",
                "https://github.com/PavelKorolev",
                "pk@example.com"
            )
            assertFailsWith(MappingException::class) {
                mapper.map(serverUser)
            }
        }
        it("reverse mapping should throw UnsupportedOperationException") {
            val user = User(
                1,
                "PavelKorolev",
                "https://avatars2.githubusercontent.com/u/4147260?s=460&v=4"
            )
            assertFailsWith(UnsupportedOperationException::class) {
                mapper.reverseMap(user)
            }
        }
    }
})