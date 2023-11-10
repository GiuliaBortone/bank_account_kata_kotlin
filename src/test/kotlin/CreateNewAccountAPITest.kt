import org.assertj.core.api.Assertions.assertThat
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

        assertEquals(201, response.statusCode())
        val validUUIDv4Regex = """[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"""
        val expectedBodyRegex = """\{"id":"$validUUIDv4Regex"\}"""
        assertThat(response.body()).matches(expectedBodyRegex)
    }
}