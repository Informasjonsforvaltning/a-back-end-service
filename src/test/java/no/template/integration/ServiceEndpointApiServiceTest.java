package no.template.integration;

import java.io.File;

import no.template.TestUtilsKt;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testcontainers.containers.output.Slf4jLogConsumer;

import static no.template.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("service")
class ServiceEndpointApiServiceTest extends AbstractDockerTestContainer{
  private final static Logger logger = LoggerFactory.getLogger(ServiceEndpointApiServiceTest.class);
  private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
  private static Slf4jLogConsumer apiLog = new Slf4jLogConsumer(logger).withPrefix("api-container");

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
/*
  @Test
  @WithMockUser(authorities = {"admin"})
  void createServiceEndpointShouldReturnBadRequestOnEmptyPost() {
    Mockito
      .when(httpServletRequestMock.getHeader("Accept"))
      .thenReturn("application/json");

    ServiceEndpoint serviceEndpoint = new ServiceEndpoint();

    ResponseEntity<Void> response = serviceEndpointsApi
      .createServiceEndpoint(httpServletRequestMock, serviceEndpoint);

    assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
  }

  @Test
  @WithMockUser(authorities = {"admin"})
  void createServiceEndpointShouldReturnCreatedAndLocationHeader() {
    Mockito
      .when(httpServletRequestMock.getHeader("Accept"))
      .thenReturn("application/json");

    ServiceEndpoint serviceEndpoint = new ServiceEndpoint();
    serviceEndpoint.setName("a-service");
    serviceEndpoint.setUri(URI.create("http://www.example.com"));

    ResponseEntity<Void> response = serviceEndpointsApi
     .createServiceEndpoint(httpServletRequestMock, serviceEndpoint);

    assertEquals(HttpStatus.CREATED, response.getStatusCode());
  }
*/
}
