import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import topinambur.Http.Companion.HTTP
import kotlin.test.assertEquals

class CreateNewAccountAPITest {
    @Test
    fun `POST empty request should return 201 and account ID` () {
        val response = HTTP.post(url = "http://localhost:8080/create-new-account")

        assertEquals(201, response.statusCode)
        val validUUIDv4Regex = """[0-9a-f]{8}-[0-9a-f]{4}-[4][0-9a-f]{3}-[89ab][0-9a-f]{3}-[0-9a-f]{12}"""
        val expectedBodyRegex = """\{"id":"$validUUIDv4Regex"\}"""
        assertThat(response.body).matches(expectedBodyRegex)
    }
}