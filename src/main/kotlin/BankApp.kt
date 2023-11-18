import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler

class BankApp {
    fun start() {
        val server = Server(8080)
        val context = ServletContextHandler(server, "/")
        context.addServlet(RouterServlet::class.java, "/*")
        server.start()
    }
}
