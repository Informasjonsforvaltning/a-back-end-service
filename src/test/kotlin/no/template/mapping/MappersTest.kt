package no.template.mapping

import no.template.GENERATED_ID_0
import no.template.createServiceEndpoint
import no.template.createServiceEndpointDB
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
