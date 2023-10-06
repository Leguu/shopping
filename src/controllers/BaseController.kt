package controllers

import domain.cart.CartRepository
import domain.product.ProductRepository
import domain.user.User
import domain.user.UserRepository
import jakarta.inject.Inject
import jakarta.servlet.annotation.WebServlet
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.servlet.http.HttpSession
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext
import kotlin.reflect.typeOf

open class BaseController : HttpServlet() {
    @Inject
    protected lateinit var productRepository: ProductRepository
    @Inject
    protected lateinit var cartRepository: CartRepository
    @Inject
    protected lateinit var userRepository: UserRepository
    @Inject
    protected lateinit var templateEngine: ITemplateEngine

    private val userIdAttribute = "userId"

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = getContext(req)

        val templateName = get(req, resp, context)

        if (templateName != null) {
            process(resp, templateName, context)
        }
    }

    open fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        return null
    }

    private fun getContext(req: HttpServletRequest): Context {
        val context = Context(req.locale)
        context.setVariable("user", getUserOrAnonymous(req))
        return context
    }

    fun tryLogin(req: HttpServletRequest, username: String, password: String) {
        val user = userRepository.validateUsernamePassword(username, password)

        val session = req.getSession(true)

        session.setAttribute(userIdAttribute, user.id)
    }

    fun getUserOrAnonymous(req: HttpServletRequest): User {
        val session = req.getSession(true)
        val userId = session.getAttribute(userIdAttribute)

        return if (userId !is Int) {
            val user = userRepository.createUser()
            session.setAttribute(userIdAttribute, user.id)
            user
        } else {
            userRepository.getUserById(userId)
        }
    }

    private fun process(res: HttpServletResponse, templateName: String) {
        val template = templateEngine.process(templateName, Context())
        res.writer.write(template)
    }

    private fun process(res: HttpServletResponse, templateName: String, context: IContext) {
        val template = templateEngine.process(templateName, context)
        res.writer.write(template)
    }

    fun getParameter(req: HttpServletRequest): String? {
        val slug = req.pathInfo.split('/').elementAtOrNull(1)
        return slug
    }
}