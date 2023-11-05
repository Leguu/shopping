package infrastructure

import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class CookieUtils {
    companion object {
        inline fun <reified T> HttpServletResponse.setCookie(key: String, value: T?) {
            val cookie = Cookie(key, Json.encodeToString(value))
            this.addCookie(cookie)
        }

        inline fun <reified T> HttpServletRequest.getCookie(key: String): T? {
            val cookie = this.cookies.find { c -> c.name == key } ?: return null

            return try {
                Json.decodeFromString<T>(cookie.value)
            } catch (_: Exception) {
                null
            }
        }
    }
}