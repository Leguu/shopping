package controllers

import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext

@WebServlet("/")
class HomeController : BaseController() {
    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        super.doGet(req, resp)
    }

    override fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        return "index"
    }
}