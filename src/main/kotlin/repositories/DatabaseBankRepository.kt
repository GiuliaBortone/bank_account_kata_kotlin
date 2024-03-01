package repositories

import java.math.BigDecimal
import java.sql.Connection
import java.sql.DriverManager
import java.util.*


class DatabaseBankRepository : BankRepository {

    private val jdbcConnectionUrl: String

    constructor(host: String, port: Int, dbName: String, user: String, password: String) {
        this.jdbcConnectionUrl = "jdbc:postgresql://$host:$port/$dbName?user=$user&password=$password"
    }

    override fun createAccount(): UUID {
        val newAccountUUID = UUID.randomUUID()

        val connection = openConnection()
        val query = connection.prepareStatement("INSERT INTO bank_account (id, balance) VALUES (?, ?)")
        query.setObject(1, newAccountUUID)
        query.setBigDecimal(2, BigDecimal.ZERO)
        query.executeUpdate()

        return newAccountUUID
    }

    override fun accountExists(accountUUID: UUID): Boolean {
        val connection = openConnection()
        val query = connection.prepareStatement("SELECT COUNT(*) as foundAccounts FROM bank_account WHERE id = ?")
        query.setObject(1, accountUUID)

        val result = query.executeQuery().apply { next() }
        return result.getInt("foundAccounts") == 1
    }

    override fun depositInto(accountUUID: UUID, amount: BigDecimal) {
        throw NonExistingAccountException(accountUUID)
    }

    override fun balanceFor(accountUUID: UUID): BigDecimal {
        throw NonExistingAccountException(accountUUID)
    }

    override fun withdrawFrom(accountUUID: UUID, amount: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    private fun openConnection(): Connection = DriverManager.getConnection(jdbcConnectionUrl)
}
