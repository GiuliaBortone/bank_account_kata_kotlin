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
        val query = connection
            .prepareStatement("INSERT INTO bank_account (id, balance) VALUES (?, ?)")
            .apply {
                setObject(1, newAccountUUID)
                setBigDecimal(2, BigDecimal.ZERO)
             }

        query.executeUpdate()
        return newAccountUUID
    }

    override fun accountExists(accountUUID: UUID): Boolean {
        val connection = openConnection()
        val query = connection
            .prepareStatement("SELECT COUNT(*) as foundAccounts FROM bank_account WHERE id = ?")
            .apply {
                setObject(1, accountUUID)
             }

        val result = query.executeQuery().apply { next() }
        return result.getInt("foundAccounts") == 1
    }

    override fun depositInto(accountUUID: UUID, amount: BigDecimal) {
        val connection = openConnection()
        val query = connection
            .prepareStatement("UPDATE bank_account SET balance = (balance + ?) WHERE id = ?")
            .apply {
                setBigDecimal(1, amount)
                setObject(2, accountUUID)
            }

        val affectedRows = query.executeUpdate()

        if (affectedRows == 0)
            throw NonExistingAccountException(accountUUID)
    }

    override fun balanceFor(accountUUID: UUID): BigDecimal {
        val connection = openConnection()
        val query = connection.prepareStatement("SELECT balance FROM bank_account WHERE id = ?")
        query.setObject(1, accountUUID)

        val resultSet = query.executeQuery()
        if (!resultSet.next())
            throw NonExistingAccountException(accountUUID)

        return resultSet.getBigDecimal("balance")
    }

    override fun withdrawFrom(accountUUID: UUID, amount: BigDecimal): Boolean {
        TODO("Not yet implemented")
    }

    private fun openConnection(): Connection = DriverManager.getConnection(jdbcConnectionUrl)
}
