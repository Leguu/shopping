package controllers

import domain.InvalidOperation
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext

@WebServlet("/login")
class LoginController : BaseController() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)
    }

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        return "login"
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val username = req.getParameter("username") ?: throw InvalidOperation()
        val password = req.getParameter("password") ?: throw InvalidOperation()

        tryLogin(req, username, password)

        resp.sendRedirect("/")
    }
}