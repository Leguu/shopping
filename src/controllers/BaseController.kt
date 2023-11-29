package controllers

import domain.cart.CartRepository
import domain.order.OrderRepository
import domain.product.ProductRepository
import domain.user.User
import domain.user.UserRepository
import infrastructure.CookieUtils.Companion.getCookie
import infrastructure.CookieUtils.Companion.setCookie
import infrastructure.database.IDatabase
import infrastructure.NotFound
import infrastructure.Unauthorized
import jakarta.inject.Inject
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.thymeleaf.ITemplateEngine
import org.thymeleaf.context.Context
import org.thymeleaf.context.IContext
import org.thymeleaf.exceptions.TemplateInputException

abstract class BaseController : HttpServlet() {
    @Inject
    protected lateinit var productRepository: ProductRepository

    @Inject
    protected lateinit var cartRepository: CartRepository

    @Inject
    protected lateinit var userRepository: UserRepository

    @Inject
    protected lateinit var templateEngine: ITemplateEngine

    @Inject
    protected lateinit var orderRepository: OrderRepository

    @Inject
    protected lateinit var database: IDatabase

    private val userIdAttribute = "userId"

    protected fun HttpServletRequest.assertIsAdmin() {
        if (getUser()?.isAdmin != true) {
            throw Unauthorized()
        }
    }

    private fun <T> HttpServletResponse.doWithErrorHandling(context: Context, doFun: () -> T): T? {
        try {
            return doFun()
        } catch (e: Unauthorized) {
            sendRedirect("/error/401")
        } catch (e: NotFound) {
            sendRedirect("/error/404")
        } catch (e: TemplateInputException) {
            sendRedirect("/error/404")
        } catch (e: Exception) {
            context.setVariable("exception", e)
            this.process("/error/500", context)
        }
        return null
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = getBasicContext(req, resp)

        resp.doWithErrorHandling(context) {
            val templateName = get(req, resp, context)
            if (templateName != null) {
                resp.process(templateName, context)
            }
        }
    }

    override fun doDelete(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = getBasicContext(req, resp)
        resp.doWithErrorHandling(context) {
            delete(req, resp)
        }
    }

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = getBasicContext(req, resp)
        resp.doWithErrorHandling(context) {
            post(req, resp)
        }
    }

    override fun doPut(req: HttpServletRequest, resp: HttpServletResponse) {
        val context = getBasicContext(req, resp)
        resp.doWithErrorHandling(context) {
            put(req, resp)
        }
    }

    open fun get(req: HttpServletRequest, resp: HttpServletResponse, context: Context): String? {
        return null
    }

    open fun post(req: HttpServletRequest, resp: HttpServletResponse) {}

    open fun delete(req: HttpServletRequest, resp: HttpServletResponse) {}

    open fun put(req: HttpServletRequest, resp: HttpServletResponse) {}

    private fun getBasicContext(req: HttpServletRequest, resp: HttpServletResponse): Context {
        val context = Context(req.locale)
        context.setVariable("user", req.getUser() ?: User(0, "", false))
        return context
    }

    fun HttpServletResponse.signOut() {
        this.setCookie(userIdAttribute, "")
    }

    fun HttpServletResponse.tryLogin(password: String) {
        val user = userRepository.validateUsernamePassword(password)

        this.setCookie(userIdAttribute, user.id)
    }

    fun HttpServletRequest.getUser(): User? {
        val userId = this.getCookie<Int>(userIdAttribute)

        return if (userId is Int) {
            userRepository.getUserById(userId)
        } else null
    }

    private fun HttpServletResponse.process(templateName: String, context: IContext) {
        val template = templateEngine.process(templateName, context)
        writer.write(template)
    }

    fun HttpServletRequest.getRouteParameters(): List<String> {
        return pathInfo.trim('/').split('/')
    }

    fun HttpServletRequest.getRouteParameter(): String {
        val param = getRouteParameters().joinToString("/")

        if (param.isBlank()) {
            throw NotFound()
        }

        return param
    }
}
