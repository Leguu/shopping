package domain.user

import infrastructure.database.Database
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DatabaseUserRepositoryTest {

    @Test
    fun setPassword() {
        val db = Database(":memory:")
        db.batch("users/UsersTableMigration")

        val userRepository = DatabaseUserRepository(db)

        val oldPass = "hahaha123"
        db.updateSql("insert into Users (password, isAdmin)" +
                "values (?, ?);", oldPass, false)

        assertEquals(oldPass, userRepository.getUserById(1)!!.password)

        val newPass = "test124542738"
        userRepository.setPassword(1, newPass)

        val actualPass = userRepository.getUserById(1)!!.password

        assertEquals(newPass, actualPass)
    }
}