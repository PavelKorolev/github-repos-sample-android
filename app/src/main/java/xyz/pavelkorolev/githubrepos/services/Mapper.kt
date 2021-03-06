package xyz.pavelkorolev.githubrepos.services

import xyz.pavelkorolev.githubrepos.models.Repository
import xyz.pavelkorolev.githubrepos.models.User
import xyz.pavelkorolev.githubrepos.network.models.ServerRepository
import xyz.pavelkorolev.githubrepos.network.models.ServerUser

abstract class Mapper<T1, T2> {
    abstract fun map(value: T1): T2
    abstract fun reverseMap(value: T2): T1

    fun map(values: List<T1>): List<T2> = values.map { map(it) }
    fun reverseMap(values: List<T2>): List<T1> = values.map { reverseMap(it) }
}

class MappingException(override val message: String?) : RuntimeException(message)

class ServerRepositoryMapper : Mapper<ServerRepository, Repository>() {

    override fun map(value: ServerRepository): Repository {
        val id = value.id ?: throw MappingException("Server repository must have id")
        val title = value.name ?: throw MappingException("Server repository must have title")
        val url = value.html_url ?: throw MappingException("Server repository must have url")
        val description = value.description
        return Repository(id, title, description, url)
    }

    override fun reverseMap(value: Repository): ServerRepository {
        throw UnsupportedOperationException()
    }

}

class ServerUserMapper : Mapper<ServerUser, User>() {

    override fun map(value: ServerUser): User {
        val id = value.id ?: throw MappingException("Server user must have id")
        val login = value.login ?: throw MappingException("Server user must have login")
        val avatarUrl = value.avatar_url
        return User(id, login, avatarUrl)
    }

    override fun reverseMap(value: User): ServerUser {
        throw UnsupportedOperationException()
    }

}
