package domain.cart

import java.sql.ResultSet

class Cart(val userId: Int, val products: MutableList<UserProductCart>)


class UserProductCart(val productId: Int?, val quantity: Int?) {
    companion object {
        fun fromResultSet(rs: ResultSet): UserProductCart {
            return UserProductCart(rs.getInt("productId"), rs.getInt("quantity"))
        }
    }
};