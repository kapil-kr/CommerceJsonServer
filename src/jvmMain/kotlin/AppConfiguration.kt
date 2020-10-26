import java.io.StringReader
import java.util.*

object AppConfiguration {
    private val properties = Properties()

    val host: String?
    val port: Int?
    val password: String

    init {
        val propContent = javaClass.getResource("/application.properties").readText()
        properties.load(StringReader(propContent))
        host = properties.getProperty("server.host")
        port = properties.getProperty("server.port")?.toInt()
        password = properties.getProperty("server.auth.password")!!
    }
}
