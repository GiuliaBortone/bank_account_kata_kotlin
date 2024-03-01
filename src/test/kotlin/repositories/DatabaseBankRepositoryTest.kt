package repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.sql.DriverManager
import java.util.*

// TODO: this tests can be executed only if a ready database
//  with created db schema is up and running on localhost
class DatabaseBankRepositoryTest {

    private val host = "localhost"
    private val port = 5432
    private val dbName = "postgres"
    private val user = "postgres"
    private val password = "mysecretpassword"

    private val repository = DatabaseBankRepository(host, port, dbName, user, password)

    @BeforeEach
    fun setUp() {
        val jdbcConnectionUrl = "jdbc:postgresql://$host:$port/$dbName?user=$user&password=$password"
        val connection = DriverManager.getConnection(jdbcConnectionUrl)
        val databaseSchema = """CREATE TABLE IF NOT EXISTS BANK_ACCOUNT (
                                id uuid PRIMARY KEY NOT NULL,
                                balance numeric NOT NULL
                             )"""

        val query = connection.prepareStatement(databaseSchema)
        query.execute()
    }

    @Test
    fun `check account exists for a non existing account`() {
        val nonExistingAccountUUID = UUID.randomUUID()
        assertThat(repository.accountExists(nonExistingAccountUUID)).isFalse()
    }

    @Test
    fun `check account exists after creation`() {
        val existingAccountUUID = repository.createAccount()
        assertThat(repository.accountExists(existingAccountUUID)).isTrue()
    }

    @Test
    fun `check balance for a non existing account`() {
        val nonExistingAccountUUID = UUID.fromString("042cd801-b941-4ec1-8cd6-e1887023fd72")
        val thrownException = assertThrows<NonExistingAccountException> {
            repository.balanceFor(nonExistingAccountUUID)
        }

        assertThat(thrownException).hasMessage("Account with 042cd801-b941-4ec1-8cd6-e1887023fd72 does not exist")
    }

    @Test
    fun `check balance for a new account`() {
        val existingAccountUUID = repository.createAccount()
        val balance = repository.balanceFor(existingAccountUUID)
        assertThat(balance).isZero()
    }

    @Test
    fun `deposit into a non existing account`() {
        val nonExistingAccountUUID = UUID.fromString("10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809")

        val thrownException = assertThrows<NonExistingAccountException> {
            repository.depositInto(nonExistingAccountUUID, BigDecimal(100))
        }

        assertThat(thrownException).hasMessage("Account with 10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809 does not exist")
    }

}
