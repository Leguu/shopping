package controllers

import domain.cart.CartRepository
import domain.product.ProductRepository
import domain.user.User
import domain.user.UserRepository
import infrastructure.NotFound
import infrastructure.Unauthorized
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext

abstract class BaseController : HttpServlet() {
    @Inject
    protected lateinit var productRepository: ProductRepository
    @Inject
    protected lateinit var cartRepository: CartRepository
    @Inject
    protected lateinit var userRepository: UserRepository
    @Inject
    protected lateinit var templateEngine: ITemplateEngine

    private val userIdAttribute = "userId"

    private fun <T>HttpServletResponse.doWithErrorHandling(doFun: () -> T): T? {
        try {
            return doFun()
        } catch (e: Unauthorized) {
            sendRedirect("/error/401")
        } catch (e: NotFound) {
            sendRedirect("/error/404")
        }
        return null
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = req.getBasicContext()

        val templateName = resp.doWithErrorHandling {
            get(req, resp, context)
        }

        if (templateName != null) {
            resp.process(templateName, context)
        }
    }

    override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.doWithErrorHandling {
            delete(req, resp)
        }
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        resp.doWithErrorHandling {
            post(req, resp)
        }
    }

    open fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? { return null }

    open fun post(req: HttpServletRequest, resp: HttpServletResponse) {}

    open fun delete(req: HttpServletRequest, resp: HttpServletResponse) {}

    private fun HttpServletRequest.getBasicContext(): Context {
        val context = Context(locale)
        context.setVariable("user", this.getUserOrAnonymous())
        return context
    }

    fun HttpServletRequest.tryLogin(username: String, password: String) {
        val user = userRepository.validateUsernamePassword(username, password)

        val session = this.getSession(true)

        session.setAttribute(userIdAttribute, user.id)
    }

    fun HttpServletRequest.getUserOrAnonymous(): User {
        val session = this.getSession(true)
        val userId = session.getAttribute(userIdAttribute)

        return if (userId !is Int) {
            val user = userRepository.createUser()
            session.setAttribute(userIdAttribute, user.id)
            user
        } else {
            userRepository.getUserById(userId)
        }
    }

    private fun HttpServletResponse.process(templateName: String, context: IContext) {
        val template = templateEngine.process(templateName, context)
        writer.write(template)
    }

    fun HttpServletRequest.getRouteParameter(): String? {
        return pathInfo.split('/').elementAtOrNull(1)
    }
}
