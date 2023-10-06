package domain.user

import infrastructure.InvalidOperation
import infrastructure.NotFound
import infrastructure.Unauthorized
import jakarta.enterprise.context.ApplicationScoped

interface UserRepository {
    fun getUserById(id: Int): User
    fun createUser(): User
    fun validateUsernamePassword(username: String, password: String): User
}

@ApplicationScoped
class InMemoryUserRepository : UserRepository {
    private var idIndex: Int = 1
    private val users: MutableList<User> = mutableListOf(
        User(idIndex++, "Admin", "secret", true)
    )

    override fun getUserById(id: Int): User {
        return users.find { u -> u.id == id } ?: throw NotFound()
    }

    override fun createUser(): User {
        val newUser = User(idIndex++, "", "", false)
        users.add(newUser)
        return newUser
    }

    override fun validateUsernamePassword(username: String, password: String): User {
        if (username == "") throw InvalidOperation("You can't log in with an empty username")
        return users.find { u -> u.name == username && u.password == password } ?: throw Unauthorized()
    }
}