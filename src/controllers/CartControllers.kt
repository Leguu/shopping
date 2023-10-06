package controllers

import domain.InvalidOperation
import domain.NotFound
import domain.Unauthorized
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/cart")
class CartsServlet : BaseController() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)
    }

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        val user = getUserOrAnonymous(req)

        context.setVariable("cart", cartRepository.getUserCart(user.id))

        return "cart"
    }
}

@WebServlet("/cart/*")
class CartServlet : BaseController() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)
    }

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        val slug = getParameter(req)

        if (slug == "add") {
            return "products/add"
        } else if (slug != null) {
            context.setVariable("product", productRepository.getProductBySlug(slug))

            return "products/product"
        }

        return null
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = getUserOrAnonymous(req)

        val sku = getParameter(req)?.toIntOrNull() ?: throw NotFound()

        cartRepository.addProductToCart(user.id, sku)

        resp.sendRedirect("/cart")
    }
}