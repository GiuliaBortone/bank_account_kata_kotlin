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
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/non-existent-uuid/balance"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(404)
    }

    @Test
    fun `get the balance of a existing account returns 200 and balance to date`() {
        val existingAccountUUID = createNewAccount()
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/$existingAccountUUID/balance"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(200)
        val responseBody = JsonParser.parseString(response.body()).asJsonObject
        assertEquals(BigDecimal.ZERO, responseBody.get("balance").asBigDecimal)
        assertValidDateTime(responseBody.get("date").asString)
    }

    private fun createNewAccount(): String {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/create-new-account"))
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(201, response.statusCode())
        val responseBody = JsonParser.parseString(response.body())
        return responseBody.asJsonObject.get("id").asString
    }

    private fun assertValidDateTime(value: String) {
        assertDoesNotThrow {
            ZonedDateTime.parse(value, DateTimeFormatter.ISO_OFFSET_DATE_TIME)
        }
    }
}