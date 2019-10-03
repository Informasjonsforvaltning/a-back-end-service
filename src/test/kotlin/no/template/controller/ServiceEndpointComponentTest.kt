package no.template.controller

import no.template.API_PORT
import no.template.API_SERVICE_NAME
import no.template.getContent
import org.junit.jupiter.api.Assertions

import no.template.integration.AbstractDockerTestContainer as ApiContainer
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag


@Tag("service")
class ServiceEndpointComponentTest : ApiContainer() {

    @Test
    fun `excpect version to return response with version object`() {
        val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                  ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                  "/version"
                )

        Assertions.assertTrue(result.getValue("status").contains("200"))
    }

}
