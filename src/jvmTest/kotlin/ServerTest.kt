import io.ktor.server.engine.*
import io.ktor.server.netty.*
import io.restassured.RestAssured
import io.restassured.RestAssured.*
import io.restassured.response.ResponseBodyExtractionOptions
import io.restassured.specification.RequestSpecification
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import java.io.File
import java.util.concurrent.TimeUnit

open class ServerTest {

    protected fun RequestSpecification.When(): RequestSpecification {
        return this.`when`()
    }

    protected inline fun <reified T> ResponseBodyExtractionOptions.to(): T {
        return this.`as`(T::class.java)
    }

    fun getFile(): File {
        return File("/store.json")
    }

    companion object {

        private var serverStarted = false
        private lateinit var server: ApplicationEngine

        @BeforeAll
        @JvmStatic
        fun startServer() {
            if(!serverStarted) {
                server = embeddedServer(Netty, 9090) {
                    serverConfig()
                }.start()
                serverStarted = true

                RestAssured.baseURI = "http://localhost"
                RestAssured.port = 9090
                RestAssured.authentication = basic("kapilTest", AppConfiguration.password)
                Runtime.getRuntime().addShutdownHook(Thread { server.stop(0, 0, TimeUnit.SECONDS) })
            }
        }
        @AfterAll
        @JvmStatic
        fun clearFile() {
            File("/store.json").writeText("{\"posts\":[],\"authors\":[]}")
        }
    }

    @BeforeEach
    fun before() = runBlocking {
        getFile().writeText("{\"posts\":[],\"authors\":[]}")
    }
}