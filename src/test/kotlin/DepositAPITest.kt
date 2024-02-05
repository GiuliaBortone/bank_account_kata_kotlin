import CreateNewAccountAPITest.Companion.createNewAccount
import org.assertj.core.api.Assertions
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import java.util.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class DepositAPITest {
    private val bankApp = BankApp()
    private val client = HttpClient
        .newBuilder()
        .build()

    @BeforeAll
    fun setUp() {
        bankApp.start()
    }

    @AfterAll
    fun stop() {
        bankApp.stop()
    }

    @Test
    fun `GET is not allowed for deposit`() {
        val existingAccountUUID = createNewAccount(client)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/deposit"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(405, response.statusCode())
    }

    @Test
    fun `deposit of a existing account returns 202 and empty body`() {
        val existingAccountUUID = createNewAccount(client)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/deposit"))
            .POST(BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(202, response.statusCode())
        assertEquals("", response.body())
    }

    @Test
    fun `deposit in a non existing account returns 404`() {
        val nonExistentAccountUUID = UUID.randomUUID()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$nonExistentAccountUUID/deposit"))
            .POST(BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, response.statusCode())
    }

    companion object {
        fun depositInto(existingAccountUUID: String, amount: Int, client: HttpClient) {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/deposit"))
                .POST(BodyPublishers.ofString(""" { "amount": $amount } """))
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())
            Assertions.assertThat(response.statusCode()).isEqualTo(202)
        }
    }
}