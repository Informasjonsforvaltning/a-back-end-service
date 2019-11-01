package no.brreg.informasjonsforvaltning.abackendservice.service


import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import no.brreg.informasjonsforvaltning.abackendservice.adapter.VersionAdapter
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpointCollection
import no.brreg.informasjonsforvaltning.abackendservice.mapping.mapForCreation
import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB
import no.brreg.informasjonsforvaltning.abackendservice.repository.ServiceEndpointRepository
import no.brreg.informasjonsforvaltning.abackendservice.utils.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test

@Tag("unit")
class ServiceEndpointServiceTest {

    private val repository: ServiceEndpointRepository = mock()
    private val adapter : VersionAdapter = mock()
    private val service : ServiceEndpointService = ServiceEndpointService(repository, adapter)

    @Test
    fun `expect getServiceEnpoints to return empty ServiceEndpointCollection`(){
        whenever(repository.findAll()).thenReturn(EMPTY_DB_LIST)
        val result = service.getServiceEndpoints()
        assertEquals(result.total,0)
        assertEquals(result.serviceEndpoints.size, 0)
    }

    @Test
    fun `expect getServiceEnpoints to return ServiceEndpointCollection with content`(){
        whenever(repository.findAll()).thenReturn(ENDPOINTS_DB_LIST)
        val result = service.getServiceEndpoints()
        val dbList = result.serviceEndpoints

        assertEquals(result.total,1)
        assertEquals(dbList.size, 1)
        assertEquals(dbList[0].name, TestNames.CORRECT)
        assertEquals(dbList[0].url.toString(),TestUrls.CORRECT)
    }

    @Test
    fun `expect createServiceEndpoint to return a ServiceEndpoint`(){
        val mappedObject = createServiceEndpoint(TestNames.CORRECT,TestUrls.CORRECT)
        val dbObject = mappedObject.mapForCreation()

        whenever(repository.save<ServiceEndpointDB>(any()))
                .thenReturn(dbObject)

        val result = service.createServiceEndpoint(mappedObject)

        assertEquals(mappedObject.name, result.name)
        assertEquals(mappedObject.url,result.url)
    }

}

