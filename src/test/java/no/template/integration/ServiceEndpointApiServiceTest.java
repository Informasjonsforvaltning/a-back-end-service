package no.template.integration;

import no.template.generated.api.ServiceEndpointApi;
import no.template.generated.model.ServiceEndpointCollection;
import no.template.generated.model.ServiceEndpoint;
import java.net.URI;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ContextConfiguration;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import javax.servlet.http.HttpServletRequest;

import static no.template.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {ServiceEndpointApiServiceTest.Initializer.class})
@Tag("service")
class ServiceEndpointApiServiceTest {
  private final static Logger logger = LoggerFactory.getLogger(ServiceEndpointApiServiceTest.class);
  private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");

  @Mock
  private static HttpServletRequest httpServletRequestMock;

  @Autowired
  private ServiceEndpointApi serviceEndpointsApi;

  @Container
  private static final GenericContainer mongoContainer = new GenericContainer("mongo:latest")
  .withEnv(getMONGO_ENV_VALUES())
  .withLogConsumer(mongoLog)
  .withExposedPorts(MONGO_PORT)
  .waitingFor(Wait.forListeningPort());

  static class Initializer
  implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
      TestPropertyValues.of(
      "spring.data.mongodb.database=" + DATABASE_NAME,
      "spring.data.mongodb.uri=" + buildMongoURI(mongoContainer.getContainerIpAddress(), mongoContainer.getMappedPort(MONGO_PORT), false)
      ).applyTo(configurableApplicationContext.getEnvironment());
    }
  }

  @Test
  void getServiceEndpointsShouldReturnAnEmptyArrayOfServiceEndpoints() {
    Mockito
    .when(httpServletRequestMock.getHeader("Accept"))
    .thenReturn("application/json");

    ResponseEntity<ServiceEndpointCollection> response = serviceEndpointsApi
    .getServiceEndpoints(httpServletRequestMock);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertEquals(Integer.valueOf(0), response.getBody().getTotal());
    assertTrue(response.getBody().getServiceEndpoints().isEmpty());
  }


  @Test
  void createServiceEndpointShouldReturnUnauthorizedWhitNoAuthentication() {
    Mockito
    .when(httpServletRequestMock.getHeader("Accept"))
    .thenReturn("application/json");

    ResponseEntity<Void> response = serviceEndpointsApi
    .createServiceEndpoint(httpServletRequestMock, new ServiceEndpoint());

    assertEquals(HttpStatus.UNAUTHORIZED, response.getStatusCode());
  }


  @Test
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

  @Test
  void getServiceEndpointsShouldReturnNonEmptyArrayOfServiceEndpoints() {
    Mockito
    .when(httpServletRequestMock.getHeader("Accept"))
    .thenReturn("application/json");

    ResponseEntity<ServiceEndpointCollection> response = serviceEndpointsApi
    .getServiceEndpoints(httpServletRequestMock);

    assertEquals(HttpStatus.OK, response.getStatusCode());
    assertTrue(response.getBody().getTotal() > Integer.valueOf(0));
    assertFalse(response.getBody().getServiceEndpoints().isEmpty());
  }
}
