package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository

class CreateNewAccountRoute: Route() {
    override fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val newAccountID = bankRepository.createAccount()
        resp.status = HttpServletResponse.SC_CREATED
        resp.writer.print("""{"id": "$newAccountID"}""")
    }


}