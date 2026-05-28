import com.ritense.valtimoplugins.openklant.client.OpenKlantClient
import com.ritense.valtimoplugins.openklant.model.KlantcontactQuery
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.web.client.RestClient
import org.springframework.web.util.UriComponentsBuilder
import java.net.URI

internal class OpenKlantClientIntegrationTest {
    private val restClientBuilder = mockk<RestClient.Builder>(relaxed = true)
    private val client = OpenKlantClient(restClientBuilder)

    @Test
    fun `buildKlantcontactUri builds correct URI with all options`() {
        val query =
            KlantcontactQuery(
                objectTypeId = "type123",
                bsn = "bsn456",
                objectUuid = "uuid789",
            )

        val builder = UriComponentsBuilder.fromUriString("https://example.com")
        val result: URI = client.buildKlantcontactUri(builder, query)

        assertEquals(
            "https://example.com/klantcontacten?" +
                    "onderwerpobject__onderwerpobjectidentificatorCodeObjecttype=type123&" +
                    "hadBetrokkene__wasPartij__partijIdentificator__objectId=bsn456&" +
                    "onderwerpobject__onderwerpobjectidentificatorObjectId=uuid789",
            result.toString(),
        )
    }

    @Test
    fun `buildKlantcontactUri builds correct URI skipping null options`() {
        val query =
            KlantcontactQuery(
                objectTypeId = null,
                bsn = "bsn456",
                objectUuid = null,
            )

        val builder = UriComponentsBuilder.fromUriString("https://example.com")
        val result = client.buildKlantcontactUri(builder, query)

        assertEquals(
            "https://example.com/klantcontacten?" +
                    "hadBetrokkene__wasPartij__partijIdentificator__objectId=bsn456",
            result.toString(),
        )
    }
}
