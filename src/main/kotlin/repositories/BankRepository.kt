package repositories

import java.math.BigDecimal
import java.util.*

interface BankRepository {
    fun createAccount(): UUID
    fun accountExists(accountUUID: UUID): Boolean
    fun depositInto(accountUUID: UUID, amount: BigDecimal)
    fun balanceFor(accountUUID: UUID): BigDecimal
    fun withdrawFrom(accountUUID: UUID, amount: BigDecimal)
}

class NonExistingAccountException(accountUUID: UUID) : Exception("Account with $accountUUID does not exist")
class InsufficientFundException(accountUUID: UUID) : Exception("Account with $accountUUID does not have enough fund")

