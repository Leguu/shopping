package domain.user

import infrastructure.*
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.sql.ResultSet

@Named("MySqlUserRepository")
@Singleton
class MySqlUserRepository : UserRepository, ResourceDatabaseTableInitializer(listOf("users/UsersTableMigration")) {
    init {
        val allNames = sequence {
            MySqlDatabase().use { db ->
                val query = db.querySql("select name from Users;")
                while (query.next()) {
                    yield(query.getString("name"))
                }
            }
        }

        val lines = ClassLoaderUtil.getResourceContent("users.csv").lines()
        for (line in lines) {
            val parts = line.split(",")
            val username = parts[0]
            val password = parts[1]

            if (password.isBlank()) continue

            if (allNames.contains(username)) continue

            try {
                createUser(username, password)
            } catch (_: Exception) {
                continue
            }
        }
    }

    override fun getUserById(id: Int): User? {
        MySqlDatabase().use { db ->
            val results = db.query("users/UserByIdQuery", id)
            if (!results.next()) return null;
            return User.fromResultSet(results)
        }
    }

    override fun createUser(name: String, password: String): User {
        MySqlDatabase().use { db ->
            db.update("users/InsertUserMutation", name, password)
            return validateUsernamePassword(name, password)
        }
    }

    override fun validateUsernamePassword(username: String, password: String): User {
        if (username == "") throw InvalidOperation("You can't log in with an empty username")
        MySqlDatabase().use { db ->
            val results = db.query("users/UserByNamePasswordQuery", username, password)
            if (!results.next()) throw Unauthorized()
            return User.fromResultSet(results)
        }
    }

    private fun User.Companion.fromResultSet(rs: ResultSet): User {
        return User(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("password"),
            rs.getBoolean("isAdmin")
        )
    }
}