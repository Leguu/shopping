package controllers

import infrastructure.InvalidOperation
import infrastructure.Unauthorized
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/recovery")
class RecoveryServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        req.getUser() ?: throw Unauthorized()

        return "orders/recovery"
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser() ?: throw Unauthorized()

        val orderId = req.getParameter("orderId").toIntOrNull() ?: throw InvalidOperation("Order ID was not provided")

        orderRepository.setOrderOwner(user.id, orderId)

        resp.sendRedirect("/orders")
    }
}