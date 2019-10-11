package no.brreg.informasjonsforvaltning.abackendservice.integration;

import java.io.File;

import no.brreg.informasjonsforvaltning.abackendservice.TestDataKt;
import no.brreg.informasjonsforvaltning.abackendservice.TestUtilsKt;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static no.brreg.informasjonsforvaltning.abackendservice.TestDataKt.API_PORT;
import static no.brreg.informasjonsforvaltning.abackendservice.TestDataKt.API_SERVICE_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("service")
@Disabled
class ServiceEndpointApiServiceTest extends AbstractDockerTestContainer{
  private final static Logger logger = LoggerFactory.getLogger(ServiceEndpointApiServiceTest.class);


  @Test
  void version() {
    String response = TestUtilsKt.simpleGet(
        TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
        TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
        "/version");
    
    assertTrue(response.contains("repositoryUrl"));
    assertTrue(response.contains("branchName"));
    assertTrue(response.contains("buildTime"));
    assertTrue(response.contains("sha"));
    assertTrue(response.contains("versionId"));
  }



  @Test
  void createServiceEndpointShouldReturnForbiddenWhenNotAdmin() {
    String response = TestUtilsKt.simpleGet(
        TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
        TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
        "/serviceendpoints");

    assertEquals("{\"total\":0,\"serviceEndpoints\":[]}", response);
  }


}
