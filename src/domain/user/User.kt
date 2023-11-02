package domain.user

class User(val id: Int, val name: String, val password: String, val isAdmin: Boolean) {
    companion object
}