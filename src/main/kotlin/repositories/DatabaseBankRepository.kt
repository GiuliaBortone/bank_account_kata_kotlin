package repositories

import java.math.BigDecimal
import java.util.*

class DatabaseBankRepository : BankRepository {
    override fun createAccount(): UUID {
        TODO("Not yet implemented")
    }

    override fun accountExists(accountUUID: UUID): Boolean {
        return false
    }

    override fun depositInto(accountUUID: UUID, amount: BigDecimal) {
        throw NonExistingAccountException(accountUUID)
    }

    override fun balanceFor(accountUUID: UUID): BigDecimal {
        TODO("Not yet implemented")
    }

    override fun withdrawFrom(accountUUID: UUID, amount: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }
}