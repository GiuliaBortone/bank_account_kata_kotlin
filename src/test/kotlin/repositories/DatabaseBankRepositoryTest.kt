package repositories

import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import java.math.BigDecimal
import java.util.*

class DatabaseBankRepositoryTest {
    @Test
    fun `deposit into a non existing account`() {
        val databaseBankRepository = DatabaseBankRepository()
        val nonExistingUUID = UUID.randomUUID()

        assertThrows<NonExistingAccountException> {
            databaseBankRepository.depositInto(nonExistingUUID, BigDecimal(100))
        }
    }
}
