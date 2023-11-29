package domain.user

import infrastructure.ClassLoaderUtil
import infrastructure.InvalidOperation
import infrastructure.ResourceDatabaseTableInitializer
import infrastructure.Unauthorized
import infrastructure.database.Database
import infrastructure.database.IDatabase
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.sql.ResultSet

@Named("MySqlUserRepository")
@Singleton
class DatabaseUserRepository() : UserRepository, ResourceDatabaseTableInitializer(listOf("users/UsersTableMigration")) {
    @Inject
    private lateinit var db: IDatabase

    constructor(db: IDatabase) : this() {
        this.db = db
    }

    init {
        if (System.getenv("env") != "testing") {
            val db = Database()

            val allPasswords = sequence {
                val query = db.querySql("select password from Users;")
                while (query.next()) {
                    yield(query.getString("password"))
                }
            }

            val lines = ClassLoaderUtil.getResourceContent("users.csv").lines()
            for (password in lines) {
                if (password.isBlank()) continue

                if (allPasswords.contains(password)) continue

                try {
                    db.updateSql(
                        "INSERT INTO Users (password, isAdmin)\n" +
                                "VALUES (?, true);", password
                    )
                } catch (_: Exception) {
                    continue
                }
            }
        }
    }

    override fun getAllUsers(): List<User> {
        val results = db.querySql("select id, password, isAdmin from users;")
        val products = mutableListOf<User>()
        while (results.next()) {
            products.add(User.fromResultSet(results))
        }
        return products
    }

    override fun setUserIsAdmin(userId: Int, isAdmin: Boolean) {
        db.updateSql(
            "update Users " +
                    "set isAdmin = ? " +
                    "where id = ?;", isAdmin, userId
        )
    }

    override fun setPassword(userId: Int, password: String) {
        db.updateSql("update Users " +
                "set password = ? " +
                "where id = ?;", password, userId)
    }

    override fun getUserById(id: Int): User? {
        val results = db.querySql("SELECT id, password, isAdmin FROM Users WHERE id = ?;", id)
        if (!results.next()) return null;
        return User.fromResultSet(results)
    }

    override fun createUser(password: String): User {
        val stmt = db.querySql("select id from users where password = ?;", password)
        if (stmt.next()) throw InvalidOperation("User already exists")
        db.updateSql(
            "INSERT INTO Users (password, isAdmin)\n" +
                    "VALUES (?, false);", password
        )
        return validateUsernamePassword(password)
    }

    override fun validateUsernamePassword(password: String): User {
        if (password == "") throw InvalidOperation("You can't log in with an empty passcode")
        val results = db.querySql("SELECT id, password, isAdmin FROM Users WHERE password = ?;", password)
        if (!results.next()) throw Unauthorized()
        return User.fromResultSet(results)
    }

    private fun User.Companion.fromResultSet(rs: ResultSet): User {
        return User(
            rs.getInt("id"),
            rs.getString("password"),
            rs.getBoolean("isAdmin")
        )
    }
}