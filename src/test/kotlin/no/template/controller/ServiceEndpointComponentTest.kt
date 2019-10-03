package no.template.controller

import no.template.Expect as expect
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
    fun `expect version to return version object`() {
        val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                  ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                  "/version"
                )
        val status = result.getValue("status")
        val body = result.getValue("body")

        expect(status).to_equal("200")
        expect(body).to_contain("repositoryUrl")
        expect(body).to_contain("branchName")
        expect(body).to_contain("buildTime")
        expect(body).to_contain("sha")
        expect(body).to_contain("versionId")
    }

}
