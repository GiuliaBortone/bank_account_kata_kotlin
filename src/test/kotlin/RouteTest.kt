import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import kotlin.test.assertEquals

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RouteTest {
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
            .POST(HttpRequest.BodyPublishers.noBody())
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        assertEquals(404, response.statusCode())
    }
}