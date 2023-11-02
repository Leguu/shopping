package domain.cart

interface CartRepository {
    fun addProductToCart(userId: Int, sku: Int)
    fun removeProductFromCart(userId: Int, sku: Int)
    fun getUserCart(userId: Int): Cart
}

