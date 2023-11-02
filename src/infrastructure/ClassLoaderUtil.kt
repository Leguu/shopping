package infrastructure


import org.apache.commons.io.IOUtils

class ClassLoaderUtil {
    companion object {
        fun getResourceContent(path: String): String {
            val stream = Companion::class.java.getResourceAsStream("/$path")  ?: throw Exception("Couldn't find $path in resources")
//            val stream = classloader.getResourceAsStream(path) ?: throw InternalError()

            return IOUtils.toString(stream, "UTF-8")
        }
    }
}