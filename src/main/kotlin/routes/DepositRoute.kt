package routes

import com.google.gson.JsonParser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository

class DepositRoute : Route() {
    override fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val accountUUID = accountUUIDIfAccountExists(req, resp, bankRepository)

        if (accountUUID == null) return

        val requestBody = req.reader.lines().toList().joinToString("\n")
        val amount = JsonParser.parseString(requestBody).asJsonObject.get("amount").asBigDecimal

        bankRepository.depositInto(accountUUID, amount)

        resp.status = HttpServletResponse.SC_ACCEPTED
    }
}