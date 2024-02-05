import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.servlet.ServletContextHandler
import repositories.BankRepository
import repositories.InMemoryBankRepository
import routes.*

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
    private val bankRepository: BankRepository = InMemoryBankRepository()

    override fun service(req: HttpServletRequest, resp: HttpServletResponse) {
        val requestURI = req.requestURI
        val route = chooseRoute(requestURI)

        if (route == null) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

        when (req.method) {
            "GET" -> {
                route.handleGet(req, resp, bankRepository)
                return
            }

            "POST" -> {
                route.handlePost(req, resp, bankRepository)
                return
            }

            else -> {
                resp.status = HttpServletResponse.SC_METHOD_NOT_ALLOWED
            }
        }
    }

    private fun chooseRoute(requestURI: String): Route? {
       val uuidRegex = "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}".toRegex()

        if (requestURI == "/create-new-account") {
            return CreateNewAccountRoute()
        }

        if (requestURI.matches("/accounts/$uuidRegex/deposit".toRegex())) {
            return DepositRoute()
        }

        if (requestURI.matches("/accounts/$uuidRegex/balance".toRegex())) {
            return BalanceRoute()
        }

        if (requestURI.matches("/accounts/$uuidRegex/withdraw".toRegex())) {
            return WithdrawRoute()
        }

        return null
    }
}