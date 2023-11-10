import org.junit.jupiter.api.Test
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpRequest.BodyPublishers
import java.net.http.HttpResponse

import kotlin.test.assertEquals

class CreateNewAccountAPITest {
    @Test
    fun `POST empty request should return 201 and account ID` () {
        val client = HttpClient.newBuilder().build()
        val request = HttpRequest.newBuilder()
            .uri(URI.create("http://localhost:8080/create-new-account"))
            .POST(BodyPublishers.noBody())
            .build()

        val response = client.send(request, HttpResponse.BodyHandlers.ofString())

        val responseStatusCode = response.statusCode()
        assertEquals(201, responseStatusCode)
        //TODO assert body risposta
    }
}