package domain.order

class Order(val orderId: Int, val address: String, val trackingId: String?, var productIds: List<Int>) {
    companion object
}