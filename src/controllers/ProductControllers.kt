package controllers

import infrastructure.InvalidOperation
import infrastructure.Unauthorized
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/products")
class ProductsServlet : BaseController() {

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        context.setVariable("products", productRepository.getAllProducts())

        return "products/index"
    }
}

@WebServlet("/products/*")
class ServletProduct : BaseController() {

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
        if (!req.getUserOrAnonymous().isAdmin) {
            throw Unauthorized()
        }

        val slug = req.getParameter("slug")
        val name = req.getParameter("name")
        val description = req.getParameter("description")
        val price = req.getParameter("price").toDoubleOrNull() ?: throw InvalidOperation()

        productRepository.createProduct(slug, name, description, price)

        resp.sendRedirect("/products/${slug}")
    }
}