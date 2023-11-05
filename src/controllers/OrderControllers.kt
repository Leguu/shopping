package controllers

import infrastructure.NotFound
import infrastructure.Unauthorized
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/orders")
class OrdersServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        val user = req.getUser() ?: throw Unauthorized()

        if (user.isAdmin) {
            context.setVariable("orders", orderRepository.getAllOrders())
        } else {
            context.setVariable("orders", orderRepository.getOrders(user.id))
        }

        return "orders/index"
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser() ?: throw Unauthorized()

        val address = req.getParameter("address")

        orderRepository.createOrder(user.id, address)

        resp.sendRedirect("/orders")
    }
}

@WebServlet("/orders/*")
class OrderServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        val slug = req.getRouteParameters()

        return if (slug.first() == "create") {
            "orders/create"
        } else if (slug.last() == "ship") {
            val orderId = slug.first().toIntOrNull() ?: throw NotFound()
            val order = orderRepository.getOrder(orderId)
            context.setVariable("order", order)
            "orders/ship"
        } else {
            val orderId = slug.first().toIntOrNull() ?: throw NotFound()
            val order = orderRepository.getOrder(orderId)

            val allProducts = productRepository.getAllProducts()
            val products = allProducts.filter { order.productIds.contains(it.sku) }

            context.setVariable("order", order)
            context.setVariable("products", products)

            "orders/order"
        }
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser() ?: throw Unauthorized()

        val slug = req.getRouteParameters()

        if (slug.first() == "create") {
            val address = req.getParameter("address")

            orderRepository.createOrder(user.id, address)
        } else if (slug.last() == "ship") {
            val orderId = slug.first().toIntOrNull() ?: throw NotFound()
            val trackingId = req.getParameter("trackingId")

            orderRepository.shipOrder(orderId, trackingId)
        }

        resp.sendRedirect("/orders")
    }
}