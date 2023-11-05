package controllers

import infrastructure.InvalidOperation
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/login")
class LoginController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        return "login"
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val username = req.getParameter("username") ?: throw InvalidOperation("Missing username")
        val password = req.getParameter("password") ?: throw InvalidOperation("Missing password")

        resp.tryLogin(username, password)

        resp.sendRedirect("/")
    }
}
@WebServlet("/signout")
class SignoutController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        resp.signOut()

        resp.sendRedirect("/")

        return null
    }
}

@WebServlet("/signup")
class SignupController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        return "signup"
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val username = req.getParameter("username") ?: throw InvalidOperation("Missing username")
        val password = req.getParameter("password") ?: throw InvalidOperation("Missing password")

        userRepository.createUser(username, password)
        resp.tryLogin(username, password)

        resp.sendRedirect("/")
    }
}