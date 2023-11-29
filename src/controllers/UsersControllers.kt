package controllers

import infrastructure.InvalidOperation
import infrastructure.Unauthorized
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/users")
class UsersController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        req.assertIsAdmin()

        context.setVariable("users", userRepository.getAllUsers())

        return "users"
    }
}

@WebServlet("/change")
class ChangePassController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        req.getUser() ?: throw Unauthorized()

        return "change"
    }

    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        val user = req.getUser() ?: throw Unauthorized()

        val password = req.getParameter("password") ?: throw InvalidOperation("Please input a new password")

        if (userRepository.getAllUsers().any { it.password == password }) {
            throw Unauthorized()
        }

        userRepository.setPassword(user.id, password)

        resp.sendRedirect("/")
    }
}

@WebServlet("/upgrade/*")
class UpgradeController : BaseController() {
    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        req.assertIsAdmin()

        val userId = req.getRouteParameter().toIntOrNull() ?: throw InvalidOperation("Missing UserID")

        userRepository.setUserIsAdmin(userId, true)

        resp.sendRedirect("/users")
    }
}

@WebServlet("/downgrade/*")
class DowngradeController : BaseController() {
    override fun post(req: HttpServletRequest, resp: HttpServletResponse) {
        req.assertIsAdmin()

        val userId = req.getRouteParameter().toIntOrNull() ?: throw InvalidOperation("Missing UserID")

        userRepository.setUserIsAdmin(userId, false)

        resp.sendRedirect("/users")
    }
}