package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class BalanceRoute: Route() {
    override fun handleGet(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        val uuidFromRequest = req.requestURI.split("/")[2]
        val accountUUID = UUID.fromString(uuidFromRequest)

        if (!bankRepository.accountExists(accountUUID)) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

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