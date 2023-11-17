import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import java.util.UUID


fun main() {
    val server = Server(8080)
    val context = ServletContextHandler(server, "/")
    context.addServlet(RouterServlet::class.java, "/*")
    server.start()
}

class RouterServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val newAccountID = UUID.randomUUID()
        resp.status = HttpServletResponse.SC_CREATED
        resp.writer.print("""{"id": "$newAccountID"}""".trimMargin())
    }
}
