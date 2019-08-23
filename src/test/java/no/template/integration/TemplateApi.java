package no.template.integration;

import no.template.TestResponseReader;
import no.template.controller.TemplateApiImpl;
import no.template.generated.model.TemplateObject;
import no.template.repository.TemplateRepository;
import org.apache.jena.graph.impl.GraphMatcher;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RIOT;
import org.junit.jupiter.api.BeforeAll;
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

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {TemplateApi.Initializer.class})
@Tag("service")
class TemplateApi {
    private final static Logger logger = LoggerFactory.getLogger(TemplateApi.class);
    private static Slf4jLogConsumer mongoLog = new Slf4jLogConsumer(logger).withPrefix("mongo-container");
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private static HttpServletRequest httpServletRequestMock;

    @Autowired
    private TemplateApiImpl templateApiImpl;

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

    @BeforeAll
    static void setup(@Autowired TemplateRepository repository) {
        RIOT.init();
        repository.save(getTEMPLATE_OBJECT_DB_0());
        repository.save(getTEMPLATE_OBJECT_DB_1());
        repository.save(getTEMPLATE_OBJECT_DB_2());
    }

    @Test
    void pingTest() {
        String response = templateApiImpl.ping().getBody();
        assertEquals("pong", response);
    }

    @Test
    void getById() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/rdf+xml");

        String response0 = templateApiImpl
            .getTemplateObjectById(httpServletRequestMock, getTEMPLATE_OBJECT_0().getId())
            .getBody();

        String response1 = templateApiImpl
            .getTemplateObjectById(httpServletRequestMock, getTEMPLATE_OBJECT_1().getId())
            .getBody();

        String response2 = templateApiImpl
            .getTemplateObjectById(httpServletRequestMock, getTEMPLATE_OBJECT_2().getId())
            .getBody();

        Model modelFromResponse0 = responseReader.parseResponse(response0, "RDF/XML");
        Model expectedResponse0 = responseReader.getExpectedResponse("getOne.ttl", "TURTLE");

        boolean matched0 = GraphMatcher.equals(expectedResponse0.getGraph(), modelFromResponse0.getGraph());

        Model modelFromResponse1 = responseReader.parseResponse(response1, "RDF/XML");
        Model expectedResponse1 = responseReader.getExpectedResponse("getOne1.ttl", "TURTLE");

        boolean matched1 = GraphMatcher.equals(expectedResponse1.getGraph(), modelFromResponse1.getGraph());

        Model modelFromResponse2 = responseReader.parseResponse(response2, "RDF/XML");
        Model expectedResponse2 = responseReader.getExpectedResponse("getOne2.ttl", "TURTLE");

        boolean matched2 = GraphMatcher.equals(expectedResponse2.getGraph(), modelFromResponse2.getGraph());

        if(!matched0) { System.out.println(response0); }
        if(!matched1) { System.out.println(response1); }
        if(!matched2) { System.out.println(response2); }

        assertTrue(matched0);
        assertTrue(matched1);
        assertTrue(matched2);
    }

    @Test
    void getByNameSeveralPossibilities() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        String response = templateApiImpl
            .getTemplateObjects(httpServletRequestMock, "ET")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "TURTLE");
        Model expectedResponse = responseReader.getExpectedResponse("searchByName.ttl", "TURTLE");

        boolean isIsmorphic = expectedResponse.isIsomorphicWith(modelFromResponse);
        if(!isIsmorphic) { System.out.println(response); }

        assertTrue(isIsmorphic);
    }

    @Test
    void getByNameSingle() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("application/ld+json");

        String response = templateApiImpl
            .getTemplateObjects(httpServletRequestMock, "FORSVARET")
            .getBody();

        Model modelFromResponse = responseReader.parseResponse(response, "JSONLD");
        Model expectedResponse = responseReader.getExpectedResponse("getOne2.ttl", "TURTLE");

        boolean matched = GraphMatcher.equals(expectedResponse.getGraph(), modelFromResponse.getGraph());

        if (!matched) {
           System.out.println(response);
        }

        assertTrue(matched);
    }

    @Test
    void createAndUpdate() {
        Mockito
            .when(httpServletRequestMock.getHeader("Accept"))
            .thenReturn("text/turtle");

        TemplateObject newTemplateObjectWithPrefLabel = getNEW_TEMPLATE_OBJECT_0();
        TemplateObject newTemplateObjectNoPrefLabel = getNEW_TEMPLATE_OBJECT_1();

        String createdId0 = templateApiImpl
            .createTemplateObject(httpServletRequestMock, newTemplateObjectWithPrefLabel)
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        String createdId1 = templateApiImpl
            .createTemplateObject(httpServletRequestMock, newTemplateObjectNoPrefLabel)
            .getHeaders()
            .getLocation()
            .getPath()
            .split("/")[2];

        ResponseEntity<Void> conflictResponseCreate = templateApiImpl.createTemplateObject(httpServletRequestMock, newTemplateObjectNoPrefLabel);
        // Unable to create template object with existing name
        assertEquals(HttpStatus.CONFLICT, conflictResponseCreate.getStatusCode());

        TemplateObject newNameElseNull = new TemplateObject();
        newNameElseNull.setName("updatedName");
        newNameElseNull.setId("idInObjectIsIgnored");

        TemplateObject updated0 = templateApiImpl
            .updateTemplateObject(httpServletRequestMock, createdId0, newNameElseNull)
            .getBody();

        TemplateObject expectedUpdate0 = getNEW_TEMPLATE_OBJECT_0();
        expectedUpdate0.setId(createdId0);
        expectedUpdate0.setName("updatedName");

        // Only name was changed
        assertEquals(updated0, expectedUpdate0);

        TemplateObject updated1 = templateApiImpl
            .updateTemplateObject(httpServletRequestMock, createdId1, getUPDATE_TEMPLATE_OBJECT())
            .getBody();

        TemplateObject expectedUpdate1 = getUPDATE_TEMPLATE_OBJECT();
        expectedUpdate1.setId(createdId1);
        expectedUpdate1.setName("Name");

        // All values except name were changed
        assertEquals(updated1, expectedUpdate1);

        ResponseEntity<TemplateObject> conflictResponseUpdate = templateApiImpl.updateTemplateObject(httpServletRequestMock, createdId0, getUPDATE_TEMPLATE_OBJECT());
        // Unable to update template object with existing name
        assertEquals(HttpStatus.CONFLICT, conflictResponseUpdate.getStatusCode());
    }
}
