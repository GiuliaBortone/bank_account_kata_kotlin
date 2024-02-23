package repositories

import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.util.*


class DatabaseBankRepository : BankRepository {

    private val url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=mysecretpassword"

    override fun createAccount(): UUID {
        return UUID.randomUUID()
    }

    override fun accountExists(accountUUID: UUID): Boolean {
        val conn: Connection = DriverManager.getConnection(url)
        val query = conn.prepareStatement("SELECT COUNT(*) FROM bank_account WHERE id = '$accountUUID'")
        val result = query.executeQuery()
        result.next()
        val foundAccountSize = result.getInt(1)
        return foundAccountSize == 1;
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