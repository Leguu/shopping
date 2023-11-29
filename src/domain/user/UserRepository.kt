package domain.user

interface UserRepository {
    fun getUserById(id: Int): User?
    fun createUser(password: String): User
    fun validateUsernamePassword(password: String): User
    fun getAllUsers(): List<User>
    fun setUserIsAdmin(userId: Int, isAdmin: Boolean)
    fun setPassword(userId: Int, password: String)
}

