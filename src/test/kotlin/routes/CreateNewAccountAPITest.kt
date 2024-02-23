package routes

import BankApp
import com.google.gson.JsonParser
import org.assertj.core.api.Assertions.assertThat
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
class CreateNewAccountAPITest {
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
    fun `GET is not allowed for createNewAccount`() {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/create-new-account"))
            .GET()
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(405, response.statusCode())
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

    private fun assertValidUUID(value: String?) {
        assertThat(UUID.fromString(value).version())
            .withFailMessage("expected UUID version 4, but found $value")
            .isEqualTo(4)
    }

    companion object {
        fun createNewAccount(client: HttpClient): String {
            val request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/create-new-account"))
                .POST(BodyPublishers.noBody())
                .build()

            val response = client.send(request, HttpResponse.BodyHandlers.ofString())

            assertEquals(201, response.statusCode())
            val responseBody = JsonParser.parseString(response.body())
            return responseBody.asJsonObject.get("id").asString
        }
    }
}