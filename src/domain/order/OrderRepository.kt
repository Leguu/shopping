package domain.order

interface OrderRepository {
    fun getOrders(userId: Int): List<Order>
    fun getAllOrders(): List<Order>
    fun getOrder(orderId: Int): Order
    fun shipOrder(orderId: Int, trackingId: String)
    fun createOrder(userId: Int, address: String)
}

