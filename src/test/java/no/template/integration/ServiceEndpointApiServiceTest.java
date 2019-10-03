package no.template.integration;

import java.io.File;

import no.template.TestUtilsKt;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import static no.template.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("service")
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
