package domain.user

interface UserRepository {
    fun getUserById(id: Int): User?
    fun createUser(name: String, password: String): User
    fun validateUsernamePassword(username: String, password: String): User
}

