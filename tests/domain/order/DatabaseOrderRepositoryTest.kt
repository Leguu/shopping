package domain.order

import infrastructure.database.Database
import org.junit.jupiter.api.Test
import kotlin.test.assertEquals

class DatabaseOrderRepositoryTest {
    @Test
    fun setOrderOwner() {
        val db = Database(":memory:")
        db.batch("orders/OrdersTableMigration")

        val orderRepository = DatabaseOrderRepository(db)

        db.updateSql("insert into Orders (address)" +
                "values (?);", "awef")

        assertEquals("awef", orderRepository.getOrder(1).address)

        orderRepository.setOrderOwner(1, 1)

        val stmt = db.querySql("select userId from Orders where orderId = ?;", 1)
        stmt.next()

        val userId = stmt.getInt("userId")

        assertEquals(1, userId)
    }
}