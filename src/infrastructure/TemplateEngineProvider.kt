package infrastructure

import jakarta.enterprise.context.ApplicationScoped
import org.thymeleaf.TemplateEngine
import org.thymeleaf.templatemode.TemplateMode
import org.thymeleaf.templateresolver.ClassLoaderTemplateResolver


@ApplicationScoped
class TemplateEngineProvider : TemplateEngine() {
    init {
        val templateResolver = ClassLoaderTemplateResolver()
        templateResolver.setTemplateMode(TemplateMode.HTML)
        templateResolver.suffix = ".html"
        templateResolver.isCacheable = false

        setTemplateResolver(templateResolver)
    }
}