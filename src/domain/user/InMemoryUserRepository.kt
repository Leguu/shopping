package domain.user

import infrastructure.InvalidOperation
import infrastructure.Unauthorized
import jakarta.enterprise.inject.Vetoed

//@Named("InMemoryUserRepository")
//@Singleton
@Vetoed
class InMemoryUserRepository : UserRepository {
    private var idIndex: Int = 1
    private val users: MutableList<User> = mutableListOf(
        User(idIndex++, "admin", "secret", true)
    )

    override fun getUserById(id: Int): User? {
        return users.find { u -> u.id == id }
    }

    override fun createUser(username: String, password: String): User {
        val newUser = User(idIndex++, username, password, false)
        users.add(newUser)
        return newUser
    }

    override fun validateUsernamePassword(username: String, password: String): User {
        if (username == "") throw InvalidOperation("You can't log in with an empty username")
        return users.find { u -> u.name == username && u.password == password } ?: throw Unauthorized()
    }
}