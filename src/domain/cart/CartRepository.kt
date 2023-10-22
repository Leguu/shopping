package domain.cart

import domain.product.Product
import domain.product.ProductRepository
import jakarta.inject.Inject
import jakarta.inject.Named
import jakarta.inject.Singleton

interface CartRepository {
    fun addProductToCart(userId: Int, sku: Int)
    fun removeProductFromCart(userId: Int, sku: Int)
    fun getUserCart(userId: Int): List<Product>
}

@Named("InMemoryCartRepository")
@Singleton
class InMemoryCartRepository : CartRepository {
    @Inject private lateinit var productRepository: ProductRepository

    private val carts: MutableList<Cart> = mutableListOf()

    override fun addProductToCart(userId: Int, sku: Int) {
        var cart = carts.find { c -> c.userId == userId }
        if (cart == null) {
            cart = Cart(userId, mutableListOf())
            carts.add(cart)
        }

        productRepository.getProduct(sku)
        cart.productIds.add(sku)
    }

    override fun removeProductFromCart(userId: Int, sku: Int) {
        val cart = carts.find { c -> c.userId == userId } ?: return

        cart.productIds.removeIf { id -> id == sku }
    }

    override fun getUserCart(userId: Int): List<Product> {
        val cart = carts.find { c -> c.userId == userId } ?: return listOf()

        return cart.productIds.map { id -> productRepository.getProduct(id) }
    }
}