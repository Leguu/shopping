package domain.order

import domain.cart.CartRepository
import infrastructure.MySqlDatabase
import infrastructure.NotFound
import infrastructure.ResourceDatabaseTableInitializer
import infrastructure.set
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton
import java.sql.ResultSet
import java.sql.Statement

@Named("MySqlOrderRepository")
@Singleton
class MySqlOrderRepository: OrderRepository, ResourceDatabaseTableInitializer(listOf("orders/OrdersTableMigration")) {
    @Inject
    private lateinit var cartRepository: CartRepository

    class OrderProduct(val orderId: Int, val productIds: MutableList<Int>)

    override fun getOrders(userId: Int): MutableList<Order> {
        MySqlDatabase().use { db ->
            val orderRs = db.querySql("select orderId, address, trackingId " +
                    "from Orders where userId = ?;", userId)

            val productsRs = db.querySql("select orderId, productId " +
                    "from OrderProduct " +
                    "where orderId in (select orderId from orders where userId = ?);", userId)

            val orderProducts = mutableListOf<OrderProduct>()
            while (productsRs.next()) {
                val orderId = productsRs.getInt("orderId")
                val productId = productsRs.getInt("productId")

                val orderProductIndex = orderProducts.indexOfFirst { it.orderId == orderId }
                if (orderProductIndex == -1) {
                    orderProducts.add(OrderProduct(orderId, mutableListOf(productId)))
                } else {
                    val previous = orderProducts[orderProductIndex]
                    previous.productIds.add(productId)

                    orderProducts.removeAt(orderProductIndex)
                    orderProducts.add(previous)
                }
            }

            val orders = mutableListOf<Order>()
            while (orderRs.next()) {
                val orderId = orderRs.getInt("orderId")
                val productIds = orderProducts.find { it.orderId == orderId }?.productIds ?: listOf()
                orders.add(Order.fromResultSet(orderRs, productIds))
            }

            return orders
        }
    }

    override fun getAllOrders(): MutableList<Order> {
        MySqlDatabase().use { db ->
            val orderRs = db.querySql("select orderId, address, trackingId from Orders;")

            val productsRs = db.querySql("select orderId, productId " +
                    "from OrderProduct " +
                    "where orderId in (select orderId from orders);")

            val orderProducts = mutableListOf<OrderProduct>()
            while (productsRs.next()) {
                val orderId = productsRs.getInt("orderId")
                val productId = productsRs.getInt("productId")

                val orderProductIndex = orderProducts.indexOfFirst { it.orderId == orderId }
                if (orderProductIndex == -1) {
                    orderProducts.add(OrderProduct(orderId, mutableListOf(productId)))
                } else {
                    val previous = orderProducts[orderProductIndex]
                    previous.productIds.add(productId)

                    orderProducts.removeAt(orderProductIndex)
                    orderProducts.add(previous)
                }
            }

            val orders = mutableListOf<Order>()
            while (orderRs.next()) {
                val orderId = orderRs.getInt("orderId")
                val productIds = orderProducts.find { it.orderId == orderId }?.productIds ?: listOf()
                orders.add(Order.fromResultSet(orderRs, productIds))
            }

            return orders
        }
    }

    override fun getOrder(orderId: Int): Order {
        MySqlDatabase().use { db ->
            val orderRs = db.querySql("select orderId, address, trackingId from Orders " +
                    "where orderId = ?;", orderId)
            if (!orderRs.next()) throw NotFound()

            val productsRs = db.querySql("select productId " +
                    "from OrderProduct " +
                    "where orderId in (select orderId from orders where orderId = ?);", orderId)

            val productIds = mutableListOf<Int>()
            while (productsRs.next()) {
                productIds.add(productsRs.getInt("productId"))
            }

            return Order.fromResultSet(orderRs, productIds)
        }
    }

    override fun shipOrder(orderId: Int, trackingId: String) {
        MySqlDatabase().use { db ->
            db.updateSql("update orders " +
                    "set trackingId = ? " +
                    "where orderId = ?;", trackingId, orderId)
        }
    }

    override fun createOrder(userId: Int, address: String) {
        val cart = cartRepository.getUserCart(userId)

        MySqlDatabase().use { db ->
            val stmt = db.con.prepareStatement("insert into Orders (userId, address) " +
                    "values (?, ?)", Statement.RETURN_GENERATED_KEYS)

            stmt.set(0, userId)
            stmt.set(1, address)

            stmt.executeUpdate()

            val keyRs = stmt.generatedKeys
            if (!keyRs.next()) throw InternalError()

            val orderId = keyRs.getInt(1)

            for (product in cart.products) {
                if (product.productId == null || product.quantity == null) throw InternalError()

                db.updateSql("insert into OrderProduct (orderId, productId, quantity) " +
                        "values (?, ?, ?);", orderId, product.productId, product.quantity)
            }
        }
    }

    private fun Order.Companion.fromResultSet(rs: ResultSet, productIds: List<Int>): Order {
        return Order(rs.getInt("orderId"), rs.getString("address"), rs.getString("trackingId"), productIds)
    }
}