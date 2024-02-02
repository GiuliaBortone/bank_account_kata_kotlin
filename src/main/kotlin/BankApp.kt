import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BankApp {
    private val server = Server(8080)

    fun start() {
        val context = ServletContextHandler(server, "/")
        context.addServlet(RouterServlet::class.java, "/*")
        server.start()
    }

    fun stop() {
        server.stop()
    }
}

class RouterServlet : HttpServlet() {

    private val existingAccountIDs = mutableListOf<String>()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val newAccountID = UUID.randomUUID()
        existingAccountIDs.add(newAccountID.toString())
        resp.status = HttpServletResponse.SC_CREATED
        resp.writer.print("""{"id": "$newAccountID"}""")
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        val uuidFromRequest = req.requestURI.split("/")[2]

        if (!existingAccountIDs.contains(uuidFromRequest)) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

        resp.status = HttpServletResponse.SC_OK
        val formattedDate = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        resp.writer.print(
            """{
                "date": "$formattedDate",
                "balance":0
            }"""
        )
    }
}