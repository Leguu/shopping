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
                db.update("carts/AddProductToCartMutation", sku, quantity + 1)
            } else {
                db.update("carts/AddProductToCartMutation", sku, 1)
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
            val rs = db.query("cart/GetUserCartQuery", userId)
            while (rs.next()) {
                val productCart = UserProductCart.fromResultSet(rs)
                result.add(productCart)
            }
        }
        return Cart(userId, result)
    }

}