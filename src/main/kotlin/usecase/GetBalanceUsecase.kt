package usecase

import repositories.BankRepository
import java.math.BigDecimal
import java.time.ZonedDateTime
import java.util.*

class GetBalanceUsecase(private val bankRepository: BankRepository) {

    fun execute(accountUUID: UUID): Balance {
        if (!bankRepository.accountExists(accountUUID))
            throw AccountNotFoundException(accountUUID)

        return Balance(
            amount = bankRepository.balanceFor(accountUUID),
            dateTime = ZonedDateTime.now()
        )
    }

    data class Balance(val amount: BigDecimal, val dateTime: ZonedDateTime)
    class AccountNotFoundException(accountUUID: UUID) : Exception("Account with $accountUUID not found")
}