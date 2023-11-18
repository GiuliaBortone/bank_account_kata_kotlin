import jakarta.servlet.http.HttpServlet
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import java.util.*


fun main() {
    BankApp().start()
}

class RouterServlet : HttpServlet() {

    override fun doPost(req: HttpServletRequest, resp: HttpServletResponse) {
        val newAccountID = UUID.randomUUID()
        resp.status = HttpServletResponse.SC_CREATED
        resp.writer.print("""{"id": "$newAccountID"}""".trimMargin())
    }
}
