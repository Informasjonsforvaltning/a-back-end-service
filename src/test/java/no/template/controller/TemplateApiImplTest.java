package no.template.controller;

import no.template.TestResponseReader;
import no.template.generated.model.TemplateObject;
import no.template.service.TemplateService;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.RIOT;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolationException;
import java.util.HashSet;
import java.util.List;

import static no.template.TestDataKt.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
@Tag("unit")
public class TemplateApiImplTest {
    private TestResponseReader responseReader = new TestResponseReader();

    @Mock
    private HttpServletRequest httpServletRequestMock;

    @Mock
    private TemplateService templateServiceMock;

    @InjectMocks
    private TemplateApiImpl templateApi;

    @BeforeAll
    public static void setup() {
        RIOT.init();
        RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(new MockHttpServletRequest()));
    }

    @BeforeEach
    public void resetMocks() {
        Mockito.reset(templateServiceMock);
    }

    @Test
    void readyTest() {
        HttpStatus statusCode = templateApi.ready().getStatusCode();
        assertEquals(HttpStatus.OK, statusCode);
    }

    @Nested
    class getTemplateObjects {

        @Test
        public void missingAcceptHeader() {
            Mockito
                .when(templateServiceMock.getTemplateObjects("123NotAccepted"))
                .thenReturn(getEMPTY_LIST());

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn(null);

            ResponseEntity<String> response = templateApi.getTemplateObjects(httpServletRequestMock, "123NotAccepted");

            assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void okWhenEmptyResult() {
            List<TemplateObject> emptyList = getEMPTY_LIST();
            Mockito
                .when(templateServiceMock.getTemplateObjects("Name does not exists"))
                .thenReturn(emptyList);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = templateApi.getTemplateObjects(httpServletRequestMock, "Name does not exists");
            Model modelFromResponse = responseReader.parseResponse(response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("emptyList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void okGetAll() {
            List<TemplateObject> list = getTEMPLATE_OBJECTS();
            Mockito
                .when(templateServiceMock.getTemplateObjects(null))
                .thenReturn(list);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = templateApi.getTemplateObjects(httpServletRequestMock, null);
            Model modelFromResponse = responseReader.parseResponse(response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getList.ttl", "TURTLE");

            boolean isIsmorphic = expectedResponse.isIsomorphicWith(modelFromResponse);
            if(!isIsmorphic) { System.out.println(response.getBody()); }

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(isIsmorphic);
        }

        @Test
        public void okByName() {
            List<TemplateObject> list = getTEMPLATE_OBJECTS();
            Mockito
                .when(templateServiceMock.getTemplateObjects("Name exists"))
                .thenReturn(list);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = templateApi.getTemplateObjects(httpServletRequestMock, "Name exists");
            Model modelFromResponse = responseReader.parseResponse(response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getList.ttl", "TURTLE");

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(expectedResponse.isIsomorphicWith(modelFromResponse));
        }

        @Test
        public void whenExceptions500() {
            Mockito
                .when(templateServiceMock.getTemplateObjects(null))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            ResponseEntity<String> response = templateApi.getTemplateObjects(httpServletRequestMock, null);

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class GetTemplateObjectById {

        @Test
        public void whenEmptyResult404() {
            Mockito
                .when(templateServiceMock.getById("123Null"))
                .thenReturn(null);

            ResponseEntity<String> response = templateApi.getTemplateObjectById(httpServletRequestMock, "123Null");

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void wrongAcceptHeader() {
            TemplateObject templateObject = getTEMPLATE_OBJECT_0();
            Mockito
                .when(templateServiceMock.getById("123NotAccepted"))
                .thenReturn(templateObject);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("application/json");

            ResponseEntity<String> response = templateApi.getTemplateObjectById(httpServletRequestMock, "123NotAccepted");

            assertEquals(HttpStatus.NOT_ACCEPTABLE, response.getStatusCode());
            assertNull(response.getBody());
        }

        @Test
        public void whenNonEmptyResult200() {
            TemplateObject templateObject = getTEMPLATE_OBJECT_0();
            Mockito
                .when(templateServiceMock.getById("5d5531e55c404500068481da"))
                .thenReturn(templateObject);

            Mockito
                .when(httpServletRequestMock.getHeader("Accept"))
                .thenReturn("text/turtle");

            ResponseEntity<String> response = templateApi.getTemplateObjectById(httpServletRequestMock, "5d5531e55c404500068481da");
            Model modelFromResponse = responseReader.parseResponse(response.getBody(), "text/turtle");

            Model expectedResponse = responseReader.getExpectedResponse("getOne.ttl", "TURTLE");

            boolean isIsmorphic = expectedResponse.isIsomorphicWith(modelFromResponse);

            if(!isIsmorphic) { System.out.println(response); }

            assertEquals(HttpStatus.OK, response.getStatusCode());
            assertTrue(isIsmorphic);
        }

        @Test
        public void whenException500() {
            Mockito
                .when(templateServiceMock.getById("123Error"))
                .thenAnswer(invocation -> {
                    throw new Exception("Test error message");
                });

            ResponseEntity<String> response = templateApi.getTemplateObjectById(httpServletRequestMock, "123Error");

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class CreateTemplateObject{

        @Test
        public void whenCreated201WithLocationHeader() {
            TemplateObject templateObject = getTEMPLATE_OBJECT_0();
            Mockito
                .when(templateServiceMock.createTemplateObject(getTEMPLATE_OBJECT_0()))
                .thenReturn(templateObject);

            ResponseEntity<Void> response = templateApi.createTemplateObject(httpServletRequestMock, getTEMPLATE_OBJECT_0());

            String expectedLocationHeader = "http://localhost/template/" + templateObject.getId();

            assertEquals(HttpStatus.CREATED, response.getStatusCode());
            assertEquals(expectedLocationHeader, response.getHeaders().getLocation().toString());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(templateServiceMock.createTemplateObject(getTEMPLATE_OBJECT_0()))
                .thenAnswer( invocation -> { throw new DuplicateKeyException("Duplicate organizationId"); });

            ResponseEntity<Void> response = templateApi.createTemplateObject(httpServletRequestMock, getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(templateServiceMock.createTemplateObject(getTEMPLATE_OBJECT_0()))
                .thenAnswer( invocation -> { throw new ConstraintViolationException(new HashSet<>()); });

            ResponseEntity<Void> response = templateApi.createTemplateObject(httpServletRequestMock, getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void whenUnexpectedException500() {
            Mockito
                .when(templateServiceMock.createTemplateObject(getTEMPLATE_OBJECT_0()))
                .thenAnswer( invocation -> { throw new Exception("Unexpected exception"); });

            ResponseEntity<Void> response = templateApi.createTemplateObject(httpServletRequestMock, getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }

    }

    @Nested
    class UpdateTemplateObject {
        @Test
        public void okWhenImplemented() {
            Mockito
                .when(templateServiceMock.updateTemplateObject("id", getTEMPLATE_OBJECT_0()))
                .thenReturn(getTEMPLATE_OBJECT_0());

            ResponseEntity<TemplateObject> response = templateApi.updateTemplateObject(httpServletRequestMock, "id", getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.OK, response.getStatusCode());
        }

        @Test
        public void notFoundWhenIdNotAvailableInDB() {
            Mockito
                .when(templateServiceMock.updateTemplateObject("id", getTEMPLATE_OBJECT_0()))
                .thenReturn(null);

            ResponseEntity<TemplateObject> response = templateApi.updateTemplateObject(httpServletRequestMock, "id", getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.NOT_FOUND, response.getStatusCode());
        }

        @Test
        public void conflictOnDuplicate() {
            Mockito
                .when(templateServiceMock.updateTemplateObject("id", getTEMPLATE_OBJECT_0()))
                .thenAnswer(invocation -> {
                    throw new DuplicateKeyException("Duplicate organizationId");
                });

            ResponseEntity<TemplateObject> response = templateApi.updateTemplateObject(httpServletRequestMock, "id", getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        }

        @Test
        public void badRequestOnValidationError() {
            Mockito
                .when(templateServiceMock.updateTemplateObject("id", getTEMPLATE_OBJECT_0()))
                .thenAnswer(invocation -> {
                    throw new ConstraintViolationException(new HashSet<>());
                });

            ResponseEntity<TemplateObject> response = templateApi.updateTemplateObject(httpServletRequestMock, "id", getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }

        @Test
        public void whenUnexpectedException500() {
            Mockito
                .when(templateServiceMock.updateTemplateObject("id", getTEMPLATE_OBJECT_0()))
                .thenAnswer(invocation -> {
                    throw new Exception("Unexpected exception");
                });

            ResponseEntity<TemplateObject> response = templateApi.updateTemplateObject(httpServletRequestMock, "id", getTEMPLATE_OBJECT_0());

            assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, response.getStatusCode());
        }
    }
}
