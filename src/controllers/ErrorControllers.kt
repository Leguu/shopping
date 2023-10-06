package controllers

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context


@WebServlet("/error/*")
class ErrorServlet : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        val errorCode = req.getRouteParameter()

        return "error/${errorCode}"
    }
}