import CreateNewAccountAPITest.Companion.createNewAccount
import com.google.gson.JsonParser
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse
import kotlin.test.Ignore
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
    fun `non existing POST route returns 404`() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/non-existing"))
            .POST(BodyPublishers.noBody())
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, response.statusCode())
    }

    @Test
    fun `non existing GET route returns 404`() {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/non-existing"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, response.statusCode())
    }

    @Ignore
    @Test
    fun `deposit of a existing account returns 202 and empty body`() {
        val existingAccountUUID = createNewAccount(client)
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/deposit"))
            .POST(BodyPublishers.ofString(""" { "amount": 200 } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(202, response.statusCode())
        assertEquals("", response.body())
    }









}