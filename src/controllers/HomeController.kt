package controllers

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context

@WebServlet("/")
class HomeController : BaseController() {
    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String {
        return "index"
    }
}