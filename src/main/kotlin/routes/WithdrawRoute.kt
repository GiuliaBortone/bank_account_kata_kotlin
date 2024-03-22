package routes

import com.google.gson.JsonParser
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import repositories.InsufficientFundException
import repositories.NonExistingAccountException

class WithdrawRoute : Route() {
    override fun handlePost(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val accountUUID = extractUUID(req)

        val requestBody = req.reader.lines().toList().joinToString("\n")
        val amount = JsonParser.parseString(requestBody).asJsonObject.get("amount").asBigDecimal

        try {
            bankRepository.withdrawFrom(accountUUID, amount)
            resp.status = HttpServletResponse.SC_ACCEPTED
        } catch (e: InsufficientFundException) {
            resp.status = HttpServletResponse.SC_FORBIDDEN
        } catch (e: NonExistingAccountException) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
        }
    }
}
