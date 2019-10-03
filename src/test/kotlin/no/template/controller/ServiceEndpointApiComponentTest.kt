package no.template.no.template.controller

import no.template.API_PORT
import no.template.API_SERVICE_NAME
import org.junit.jupiter.api.Nested
import no.template.integration.AbstractDockerTestContainer as ApiContainer
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.TestInstance
import no.template.getContent
import no.template.simplePost
import no.template.jsonServiceEndpointObject as jsonObject
import no.template.Expect as expect

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("service")
class ServiceEndpointApiComponentTest : ApiContainer(){


    @Nested
    inner class postServiceEndpoint {
        @Test
        fun `expect post to return 401`(){
            val result = simplePost(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    "/serviceendpoints",
                    jsonObject("servethedata")
            )

            val status = result.getValue("status")
            expect(status).to_contain("401")
        }
    }


    @Nested
    inner class getServiceEndpoints {
        @Test
        fun `expect a ServiceEndpointCollection`() {
            val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    "/serviceendpoints"
            )
            val body = result.getValue("body")

            expect(body).to_contain("total")
            expect(body).to_contain("serviceEndpoints")
        }
    }

}