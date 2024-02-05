import CreateNewAccountAPITest.Companion.createNewAccount
import DepositAPITest.Companion.depositInto
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.util.*
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class WithdrawAPITest {
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
    fun `GET is not allowed for withdraw`() {
        val existingAccountUUID = createNewAccount(client)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/withdraw"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(405, response.statusCode())
    }

    @Test
    fun `withdraw from an existing empty account returns 403`() {
        val existingAccountUUID = createNewAccount(client)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/withdraw"))
            .POST(HttpRequest.BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(403, response.statusCode())
    }

    @Test
    fun `withdraw from a non existing account returns 404`() {
        val nonExistentAccountUUID = UUID.randomUUID()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$nonExistentAccountUUID/withdraw"))
            .POST(HttpRequest.BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, response.statusCode())
    }

    @Test
    fun `withdraw from an existing account same amount as balance returns 202`() {
        val existingAccountUUID = createNewAccount(client)
        depositInto(existingAccountUUID, 100, client)

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/withdraw"))
            .POST(HttpRequest.BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(202, response.statusCode())
    }

    @Test
    fun `withdraw from an existing account more amount than balance returns 202`() {
        val existingAccountUUID = createNewAccount(client)
        depositInto(existingAccountUUID, 300, client)

        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/withdraw"))
            .POST(HttpRequest.BodyPublishers.ofString(""" { "amount": 100 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(202, response.statusCode())
    }

}