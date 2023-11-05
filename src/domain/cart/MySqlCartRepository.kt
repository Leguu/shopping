package domain.cart

import infrastructure.MySqlDatabase
import infrastructure.ResourceDatabaseTableInitializer
import jakarta.inject.Named
import jakarta.inject.Singleton

@Named("MySqlCartRepository")
@Singleton
class MySqlCartRepository : CartRepository, ResourceDatabaseTableInitializer(listOf("carts/CartTableMigration")) {
    override fun addProductToCart(userId: Int, sku: Int) {
        MySqlDatabase().use { db ->
            val currentValue = db.querySql("select (quantity) from UserProductCart where userId = ? and productId = ?;", userId, sku)
            if (currentValue.next()) {
                val quantity = currentValue.getInt("quantity")
                db.updateSql("update UserProductCart " +
                        "set quantity = ? " +
                        "where productId = ? and userId = ?;", quantity + 1, sku, userId)
            } else {
                db.updateSql("insert into UserProductCart (userId, productId, quantity) " +
                        "values (?, ?, ?);", userId, sku, 1)
            }
        }
    }

    override fun removeProductFromCart(userId: Int, sku: Int) {
        MySqlDatabase().use { db->
            db.updateSql("delete from UserProductCart where userId = ? and productId = ?;", userId, sku)
        }
    }

    override fun getUserCart(userId: Int): Cart {
        val result = mutableListOf<UserProductCart>()
        MySqlDatabase().use { db ->
            val rs = db.querySql("select productId, quantity from UserProductCart where userId = ?;", userId)
            while (rs.next()) {
                val productCart = UserProductCart.fromResultSet(rs)
                result.add(productCart)
            }
        }
        return Cart(userId, result)
    }
}