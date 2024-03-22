package repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.sql.DriverManager
import java.util.*

// this tests can be executed only if a ready
// postgresql instance is up and running on localhost
class DatabaseBankRepositoryTest {

    companion object {
        private const val HOST = "localhost"
        private const val PORT = 5432
        private const val DB_NAME = "postgres"
        private const val DB_USER = "postgres"
        private const val DB_PASSWORD = "mysecretpassword"

        @JvmStatic
        @BeforeAll
        fun createDatabaseSchema() {
            val databaseSchemaFileContent = readFileFromResources("database_schema.sql")
            executeStatement(databaseSchemaFileContent)
        }

        private fun executeStatement(statement: String) {
            val jdbcConnectionUrl = "jdbc:postgresql://$HOST:$PORT/$DB_NAME?user=$DB_USER&password=$DB_PASSWORD"
            val connection = DriverManager.getConnection(jdbcConnectionUrl)
            val query = connection.prepareStatement(statement)
            query.execute()
        }

        private fun readFileFromResources(resourceFilepath: String) =
            Companion::class.java.classLoader.getResource(resourceFilepath)!!.readText()

    }

    @BeforeEach
    fun setUp() {
        executeStatement("TRUNCATE TABLE bank_account")
    }

    private val repository = DatabaseBankRepository(HOST, PORT, DB_NAME, DB_USER, DB_PASSWORD)

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
    fun `check balance for an account with deposited money`() {
        val existingAccountUUID = repository.createAccount()
        repository.depositInto(existingAccountUUID, BigDecimal(100))
        val balance = repository.balanceFor(existingAccountUUID)

        assertThat(balance).isEqualTo(BigDecimal(100))
    }

    @Test
    fun `deposit into a non existing account`() {
        val nonExistingAccountUUID = UUID.fromString("10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809")

        val thrownException = assertThrows<NonExistingAccountException> {
            repository.depositInto(nonExistingAccountUUID, BigDecimal(100))
        }

        assertThat(thrownException).hasMessage("Account with 10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809 does not exist")
    }

    @Test
    fun `deposit two times into same account`() {
        val existingAccountUUID = repository.createAccount()
        repository.depositInto(existingAccountUUID, BigDecimal(100))
        repository.depositInto(existingAccountUUID, BigDecimal(200))
        val balance = repository.balanceFor(existingAccountUUID)

        assertThat(balance).isEqualTo(BigDecimal(100 + 200))
    }

    @Test
    fun `withdraw from non existing account`() {
        val nonExistingAccountUUID = UUID.fromString("dac80052-4b24-468e-b11c-c0836c715eb3")

        val thrownException = assertThrows<NonExistingAccountException> {
            repository.withdrawFrom(nonExistingAccountUUID, BigDecimal(100))
        }

        assertThat(thrownException).hasMessage("Account with dac80052-4b24-468e-b11c-c0836c715eb3 does not exist")
    }

    @Test
    fun `withdraw from empty account`() {
        val existingAccountUUID = repository.createAccount()

        val thrownException = assertThrows<InsufficientFundException> {
            repository.withdrawFrom(existingAccountUUID, BigDecimal(100))
        }

        assertThat(thrownException).hasMessage("Account with $existingAccountUUID does not have enough fund")
    }

    @Test
    fun `multiple accounts scenario`() {
        val firstAccountUUID = repository.createAccount()
        val secondAccountUUID = repository.createAccount()

        assertThat(repository.balanceFor(firstAccountUUID)).isZero()
        assertThat(repository.balanceFor(secondAccountUUID)).isZero()

        repository.depositInto(firstAccountUUID, BigDecimal(100))
        repository.depositInto(secondAccountUUID, BigDecimal(200))

        assertThat(repository.balanceFor(firstAccountUUID)).isEqualTo(BigDecimal(100))
        assertThat(repository.balanceFor(secondAccountUUID)).isEqualTo(BigDecimal(200))
    }
}
