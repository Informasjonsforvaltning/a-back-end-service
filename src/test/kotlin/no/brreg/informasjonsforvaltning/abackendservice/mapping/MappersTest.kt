package no.brreg.informasjonsforvaltning.abackendservice.mapping

import no.brreg.informasjonsforvaltning.abackendservice.utils.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.assertEquals

@Tag("unit")
class MappersTest {

    @Test

    fun `expect mapToGenerated to return a ServiceEndpoint`() {
        val serviceEndpointDB = createServiceEndpointDB(TestNames.CORRECT, TestUrls.CORRECT)
        val expected = createServiceEndpoint(TestNames.CORRECT,TestUrls.CORRECT)

        assertEquals(expected, serviceEndpointDB.mapToGenerated())
    }

    @Test
    fun `expect mapForCreation to return a ServiceEndpoinDBt`() {
        val serviceEndpoint = createServiceEndpoint(TestNames.CORRECT, TestUrls.CORRECT)
        val expected = createServiceEndpointDB(TestNames.CORRECT,TestUrls.CORRECT)
        val result = serviceEndpoint.mapForCreation()

        assertEquals(expected.name, result.name)
        assertEquals(expected.url, result.url)
    }
 }
