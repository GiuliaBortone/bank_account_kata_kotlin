import CreateNewAccountAPITest.Companion.createNewAccount
import com.google.gson.JsonParser
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.*
import java.math.BigDecimal
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AccountBalanceAPITest {
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
    fun `get the balance of a non existent account returns 404`() {
        val request = getRequest("/accounts/non-existent-uuid/balance")

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(404)
    }

    @Test
    fun `get the balance of a existing account returns 200 and balance to date`() {
        val existingAccountUUID = createNewAccount(client)
        val request = getRequest("/accounts/$existingAccountUUID/balance")

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(200)
        val responseBody = JsonParser.parseString(response.body()).asJsonObject
        assertEquals(BigDecimal.ZERO, responseBody.get("balance").asBigDecimal)
        assertValidDateTime(responseBody.get("date").asString)
    }

    @Test
    fun `get balance after deposit returns updated balance`() {
        val existingAccountUUID = createNewAccount(client)
        depositInto(existingAccountUUID, 100)
        val request = getRequest("/accounts/$existingAccountUUID/balance")

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(200)
        val responseBody = JsonParser.parseString(response.body()).asJsonObject
        assertEquals(BigDecimal(100), responseBody.get("balance").asBigDecimal)
    }

    private fun depositInto(existingAccountUUID: String, amount: Int) {
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/deposit"))
            .POST(HttpRequest.BodyPublishers.ofString(""" { "amount": $amount } """))
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        assertThat(response.statusCode()).isEqualTo(202)
    }


    private fun getRequest(uriPath: String): HttpRequest {
        return HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080$uriPath"))
            .GET()
            .build()
    }

    private fun assertValidDateTime(value: String) {
        assertDoesNotThrow {
            ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }
}