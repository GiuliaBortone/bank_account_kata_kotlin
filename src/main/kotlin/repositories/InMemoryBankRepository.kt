package repositories

import java.math.BigDecimal
import java.util.*

class InMemoryBankRepository : BankRepository {
    private val accountBalances = mutableMapOf<UUID, BigDecimal>()

    override fun createAccount(): UUID {
        val newAccountUUID = UUID.randomUUID()
        accountBalances[newAccountUUID] = BigDecimal.ZERO

        return newAccountUUID
    }

    override fun accountExists(accountUUID: UUID): Boolean {
        return accountBalances.containsKey(accountUUID)
    }

    override fun depositInto(accountUUID: UUID, amount: BigDecimal) {
        accountBalances[accountUUID] = accountBalances[accountUUID]!! + amount
    }

    override fun balanceFor(accountUUID: UUID): BigDecimal {
        return accountBalances[accountUUID]!!
    }
}
