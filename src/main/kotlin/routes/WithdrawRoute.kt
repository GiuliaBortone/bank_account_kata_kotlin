package routes

import com.google.gson.JsonParser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import java.util.*

class WithdrawRoute : Route() {
    override fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val uuidFromRequest = req.requestURI.split("/")[2]
        val accountUUID = UUID.fromString(uuidFromRequest)

        if (!bankRepository.accountExists(accountUUID)) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

        val requestBody = req.reader.lines().toList().joinToString("\n")
        val amount = JsonParser.parseString(requestBody).asJsonObject.get("amount").asBigDecimal

        if (!bankRepository.withdrawFrom(accountUUID, amount)) {
            resp.status = HttpServletResponse.SC_FORBIDDEN
            return
        }

        resp.status = HttpServletResponse.SC_ACCEPTED
    }
}
