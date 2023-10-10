package controllers

import infrastructure.NotFound
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/cart")
class CartsServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        val user = req.getUserOrAnonymous()

        context.setVariable("cart", cartRepository.getUserCart(user.id))

        return "cart"
    }
}

@WebServlet("/cart/*")
class CartServlet : BaseController() {

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        val slug = req.getRouteParameter()

        if (slug == "add") {
            return "products/add"
        } else if (slug != null) {
            context.setVariable("product", productRepository.getProductBySlug(slug))

            return "products/product"
        }

        return null
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUserOrAnonymous()

        val sku = req.getRouteParameter()?.toIntOrNull() ?: throw NotFound()

        cartRepository.addProductToCart(user.id, sku)

        resp.sendRedirect("/cart")
    }

    override fun delete(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUserOrAnonymous()

        val sku = req.getRouteParameter()?.toIntOrNull() ?: throw NotFound()

        cartRepository.removeProductFromCart(user.id, sku)

        resp.setHeader("HX-Refresh", "true")
    }
}