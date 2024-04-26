package routes

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import repositories.BankRepository
import usecase.GetBalanceUsecase
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class BalanceRoute: Route() {
    override fun handleGet(req: HttpServletRequest, resp: HttpServletResponse, bankRepository: BankRepository) {
        try {
            val usecase = GetBalanceUsecase(bankRepository) // inject me ?
            val accountUUID = extractUUID(req)
            val balance = usecase.execute(accountUUID)
            val formattedDate = balance.dateTime.format(DateTimeFormatter.ISO_OFFSET_DATE_TIME)
            resp.status = HttpServletResponse.SC_OK
            resp.writer.print(
                """{
                    "date": "$formattedDate",
                    "balance": ${balance.amount}
                }"""
            )
        } catch (ex: GetBalanceUsecase.AccountNotFoundException) {
            resp.status = HttpServletResponse.SC_NOT_FOUND
            return
        }

    }
}