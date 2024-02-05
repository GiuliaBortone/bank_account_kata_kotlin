package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BalanceRoute: Route() {
    override fun handleGet(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val accountUUID = accountUUIDIfAccountExists(req, resp, bankRepository)

        if (accountUUID == null) return

        resp.status = HttpServletResponse.SC_OK
        val formattedDate = ZonedDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        val accountBalance = bankRepository.balanceFor(accountUUID)

        resp.writer.print(
            """{
                    "date": "$formattedDate",
                    "balance": $accountBalance
                }"""
        )
    }
}