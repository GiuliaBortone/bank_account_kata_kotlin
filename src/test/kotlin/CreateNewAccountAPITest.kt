import com.google.gson.JsonParser
import org.assertj.core.api.Assertions.assertThat
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
class CreateNewAccountAPITest {
    @BeforeAll
    fun setUp() {
        BankApp().start()
    }

    @Test
    fun `POST empty request should return 201 and valid account ID`() {
        // arrange
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/create-new-account"))
            .POST(BodyPublishers.noBody())
            .build()
        // act
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        // assert
        assertEquals(201, response.statusCode())
        val responseBody = JsonParser.parseString(response.body())
        assertValidUUID(responseBody.asJsonObject.get("id").asString)
    }

    @Test
    fun `get a non existent account returns 404`() {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/accounts/non-existent-uuid/statement"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertThat(response.statusCode()).isEqualTo(404)
    }

    private fun assertValidUUID(value: String?) {
        assertThat(UUID.fromString(value).version()).withFailMessage("expected UUID version 4, but found $value ")
            .isEqualTo(4)
    }
}