package no.template.no.template.controller

import no.template.*
import no.template.integration.AbstractDockerTestContainer as ApiContainer
import org.junit.jupiter.api.*
import no.template.jsonServiceEndpointObject as jsonObject
import no.template.Expect as expect

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("service")
class ServiceEndpointApiComponentTest : ApiContainer(){


    @Nested
    inner class postServiceEndpoint {
        @Test
        fun `expect post to return 401 for unauthenticated users`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                                    SERVICE_ENDPOINT,
                                    jsonObject("uniqeservice"))

            val status = result.getValue("status")
            expect(status).to_contain("401")
        }

        /*
        /** TODO get non-admin token**/
        @Test
        fun `expect post to return 403 for non-admin users`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                                    SERVICE_ENDPOINT,
                                    jsonObject("nonadmin"),
                                    NON_ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            expect(status).to_contain("403")
        }

        /** TODO get admin token**/
        @Test
        fun `expect post to return 201 response for correct request`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    jsonObject("newservice"),
                    ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            expect(status).to_contain("201")

        }

        @Test
        fun `expect post to return 400 for request with empty body`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                                    SERVICE_ENDPOINT,
                                    token = ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            expect(status).to_contain("400")
        }
        @Test

        fun `expect post to return 400 for request with missing uri`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    jsonObject("missinguriservice",
                                      addUri = false),
                    token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            expect(status).to_contain("400")
        }
        @Test
        fun `expect post to return 400 for request with missing name`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    jsonObject("missingnameservice",
                            addName = false),
                            token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            expect(status).to_contain("400")
        }

        @Test
        fun `expect post to return 409 for duplicate service name`() {

            val setup_result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    jsonObject("duplicateservice"),
                    ADMIN_TOKEN_TMP)

            val setup_status = setup_result.getValue("status")
            Assumptions.assumeTrue(setup_status.equals("200"))


            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    jsonObject("duplicateservice"),
                    token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            expect(status).to_contain("409")
        }*/
    }
/*
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
    }*/

}