package no.brreg.abackendservice.service;

import no.brreg.abackendservice.adapter.VersionAdapter;
import no.brreg.abackendservice.generated.model.ServiceEndpoint;
import no.brreg.abackendservice.generated.model.ServiceEndpointCollection;
import no.brreg.abackendservice.generated.model.Version;
import no.brreg.abackendservice.model.ServiceEndpointDB;
import no.brreg.abackendservice.repository.ServiceEndpointRepository;
import org.bson.types.ObjectId;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static no.brreg.abackendservice.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
class ServiceEndpointServiceTest {

    @Mock
    ServiceEndpointRepository repositoryMock;

    @Mock
    VersionAdapter adapterMock;

    @InjectMocks
    ServiceEndpointService serviceEndpointService;

    @Test
    void emptyCollectionWhenNotFoundInDB() {
        Mockito.when(repositoryMock.findAll())
            .thenReturn(getEMPTY_DB_LIST());

        ServiceEndpointCollection result = serviceEndpointService.getServiceEndpoints();

        assertEquals(0, result.getTotal());
        assertEquals(getEMPTY_ENDPOINTS_LIST(), result.getServiceEndpoints());
    }

    @Test
    void mappingToCollectionIsCorrect() {
        Mockito.when(repositoryMock.findAll())
            .thenReturn(getENDPOINTS_DB_LIST());

        Mockito.when(adapterMock.getVersionData(Mockito.any()))
            .thenReturn(getVERSION_DATA());

        ServiceEndpointCollection result = serviceEndpointService.getServiceEndpoints();

        assertEquals(1, result.getTotal());
        assertEquals(getENDPOINTS_LIST(), result.getServiceEndpoints());
    }

    @Test
    void saveToDatabase() {
        ServiceEndpointDB saved = createServiceEndpointDB(ObjectId.get());
        Mockito.when(repositoryMock.save(Mockito.any()))
            .thenReturn(saved);

        ServiceEndpoint result = serviceEndpointService.createServiceEndpoint(createServiceEndpoint(null));

        ServiceEndpoint expected = createServiceEndpoint(saved.getId().toHexString());

        assertEquals(expected, result);
    }
}