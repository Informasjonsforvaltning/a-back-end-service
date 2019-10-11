package no.brreg.informasjonsforvaltning.abackendservice.mapping

import no.brreg.informasjonsforvaltning.abackendservice.no.brreg.informasjonsforvaltning.abackendservice.utils.GENERATED_ID_0
import no.brreg.informasjonsforvaltning.abackendservice.no.brreg.informasjonsforvaltning.abackendservice.utils.createServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.no.brreg.informasjonsforvaltning.abackendservice.utils.createServiceEndpointDB
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull

@Tag("unit")
class MappersTest {

    @Test
    fun fromDatabaseObjectToGenerated() {
        val serviceEndpointDB = createServiceEndpointDB(GENERATED_ID_0)
        val expected = createServiceEndpoint(GENERATED_ID_0.toHexString())

        assertEquals(expected, serviceEndpointDB.mapToGenerated())
    }

    @Test
    fun mapForCreationOfDatabaseObject() {
        val serviceEndpoint = createServiceEndpoint(GENERATED_ID_0.toHexString())
        val expected = createServiceEndpointDB(GENERATED_ID_0)
        val result = serviceEndpoint.mapForCreation()

        assertNull(result.id)
        assertEquals(expected.name, result.name)
        assertEquals(expected.name, result.name)
    }
}
