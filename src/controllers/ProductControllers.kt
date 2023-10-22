package controllers

import infrastructure.InvalidOperation
import infrastructure.NotFound
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
        val params = req.getRouteParameters()
        val action = params.lastOrNull()

        if (action == "add") {
            req.assertIsAdmin()
            return "products/add"
        } else if (action == "download") {
            req.assertIsAdmin()
            val productsCatalog = productRepository.getProductCatalog()
            resp.contentType = "text/csv"
            resp.writer.write(productsCatalog)
        } else if (action == "edit") {
            val slug = params.firstOrNull() ?: throw NotFound()
            context.setVariable("product", productRepository.getProductBySlug(slug))
            return "products/edit"
        } else if (action != null) {
            context.setVariable("product", productRepository.getProductBySlug(action))

            return "products/product"
        }

        return null
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        req.assertIsAdmin()

        val params = req.getRouteParameters()
        val action = params.lastOrNull() ?: throw InvalidOperation("Missing slug")

        if (action == "edit") {
            val slug = params.firstOrNull() ?: throw InvalidOperation("Missing slug")

            val name = req.getParameter("name")
            val description = req.getParameter("description")
            val price = req.getParameter("price").toDoubleOrNull() ?: throw InvalidOperation("Price isn't a number")

            val product = productRepository.getProductBySlug(slug)

            productRepository.updateProduct(product.sku, name, description, price)

            resp.sendRedirect("/products/${slug}")
        } else if (action != "null") {
            val slug = req.getParameter("slug")
            val name = req.getParameter("name")
            val description = req.getParameter("description")
            val price = req.getParameter("price").toDoubleOrNull() ?: throw InvalidOperation("Price isn't a number")

            productRepository.createProduct(slug, name, description, price)

            resp.sendRedirect("/products/${slug}")
        }
    }
}