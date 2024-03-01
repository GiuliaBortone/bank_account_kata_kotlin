package repositories

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.sql.DriverManager
import java.util.*
import kotlin.test.Ignore

// TODO: this tests can be executed only if a ready database
//  with created db schema is up and running on localhost
class DatabaseBankRepositoryTest {

    @BeforeEach
    fun setUp() {
        val url = "jdbc:postgresql://localhost:5432/postgres?user=postgres&password=mysecretpassword"
        val connection = DriverManager.getConnection(url)
        val databaseSchema = """CREATE TABLE IF NOT EXISTS BANK_ACCOUNT (
                                id uuid PRIMARY KEY NOT NULL,
                                balance numeric NOT NULL
                                )
                             """

        val query = connection.prepareStatement(databaseSchema)
        query.execute()
    }

    private val repository = DatabaseBankRepository()

    @Test
    fun `deposit into a non existing account`() {
        val nonExistingAccountUUID = UUID.fromString("10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809")

        val thrownException = assertThrows<NonExistingAccountException> {
            repository.depositInto(nonExistingAccountUUID, BigDecimal(100))
        }

        assertThat(thrownException).hasMessage("Account with 10fea0c2-0bc6-4ebe-9f05-ee3bdfe99809 does not exist")
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

}
