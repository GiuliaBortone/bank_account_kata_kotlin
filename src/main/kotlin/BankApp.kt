import com.google.gson.JsonParser
import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import java.math.BigDecimal
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
    private val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()
    private val accountBalances = mutableMapOf<String, BigDecimal>()

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        if (req.requestURI == "/create-new-account") {
            val newAccountID = UUID.randomUUID()
            accountBalances[newAccountID.toString()] = BigDecimal.ZERO
            resp.status = HttpServletResponse.SC_CREATED
            resp.writer.print("""{"id": "$newAccountID"}""")
            return
        }

        if (req.requestURI.matches("/accounts/$uuidRegex/deposit".toRegex())) {
            val uuidFromRequest = req.requestURI.split("/")[2]

            if (!accountBalances.containsKey(uuidFromRequest)) {
                resp.status = HttpServletResponse.SC_NOT_FOUND
                return
            }

            val requestBody = req.reader.lines().toList().joinToString("\n")
            val amount = JsonParser.parseString(requestBody).asJsonObject.get("amount").asBigDecimal

            accountBalances[uuidFromRequest] = accountBalances[uuidFromRequest]!! + amount

            resp.status = HttpServletResponse.SC_ACCEPTED
            return
        }

        resp.status = HttpServletResponse.SC_NOT_FOUND
    }

    override fun doGet(req: HttpServletRequest, resp: HttpServletResponse) {
        if (req.requestURI.matches("/accounts/$uuidRegex/balance".toRegex())) {
            val uuidFromRequest = req.requestURI.split("/")[2]

            if (!accountBalances.containsKey(uuidFromRequest)) {
                resp.status = HttpServletResponse.SC_NOT_FOUND
                return
            }

            resp.status = HttpServletResponse.SC_OK
            val formattedDate = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            resp.writer.print(
                """{
                    "date": "$formattedDate",
                    "balance": ${accountBalances[uuidFromRequest]}
                }"""
            )
            return
        }

        resp.status = HttpServletResponse.SC_NOT_FOUND
    }
}