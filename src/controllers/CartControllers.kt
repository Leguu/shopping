package controllers

import domain.cart.UserProductCart
import infrastructure.NotFound
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

fun HttpServletRequest.getCartOrAnonymous(): MutableList<UserProductCart> {
    val session = this.getSession(true)
    var existingCart = session.getAttribute("userCart")

    if (existingCart == null || existingCart !is List<*>) {
        existingCart = mutableListOf<UserProductCart>()
        this.setAttribute("userCart", existingCart)
        return existingCart
    }

    return existingCart.filterIsInstance<UserProductCart>().toMutableList()
}

fun HttpServletRequest.setCart(cart: List<UserProductCart>) {
    val session = this.getSession(true)
    session.setAttribute("userCart", cart)
}

@WebServlet("/cart")
class CartsServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        val user = req.getUser()

        val products = this.productRepository.getAllProducts()

        if (user == null) {
            val cart = req.getCartOrAnonymous()
            val cartProducts = cart.map { it.productId }.map { products.find { p -> p.sku == it } }
            context.setVariable("cart", cartProducts)
        } else {
            val cart = cartRepository.getUserCart(user.id)
            val cartProducts = cart.products.map { it.productId }.map { products.find { p -> p.sku == it } }
            context.setVariable("cart", cartProducts)
        }

        return "cart"
    }
}

@WebServlet("/cart/*")
class CartServlet : BaseController() {

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        val slug = req.getRouteParameter()

        return if (slug == "add") {
            "products/add"
        } else {
            context.setVariable("product", productRepository.getProductBySlug(slug))

            "products/product"
        }
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser()

        val sku = req.getRouteParameter().toIntOrNull() ?: throw NotFound()

        if (user == null) {
            val cart = req.getCartOrAnonymous()

            val existingQuantity = cart.find { it.productId == sku }?.quantity ?: 0
            cart.removeIf { it.productId == sku }

            cart.add(UserProductCart(sku, existingQuantity + 1))

            req.setCart(cart)
        } else {
            cartRepository.addProductToCart(user.id, sku)
        }

        resp.sendRedirect("/cart")
    }

    override fun delete(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser()

        val sku = req.getRouteParameter().toIntOrNull() ?: throw NotFound()

        if (user == null) {
            val cart = req.getCartOrAnonymous()

            cart.removeIf { it.productId == sku }

            req.setCart(cart)
        } else {
            cartRepository.removeProductFromCart(user.id, sku)
        }

        resp.setHeader("HX-Refresh", "true")
    }
}