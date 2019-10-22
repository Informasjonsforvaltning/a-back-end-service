package no.brreg.informasjonsforvaltning.abackendservice.contract

import no.brreg.informasjonsforvaltning.abackendservice.utils.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.simplePost
import org.junit.BeforeClass
import no.brreg.informasjonsforvaltning.abackendservice.utils.AbstractDockerTestContainer as ApiContainer
import no.brreg.informasjonsforvaltning.abackendservice.utils.jsonServiceEndpointObject as mapServiceToJson
import no.brreg.informasjonsforvaltning.abackendservice.utils.Expect as expect
import org.junit.jupiter.api.*


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("service")
class ServiceEndpointApiContractTest : ApiContainer(){
    val authMock = AuthMock()

    @BeforeAll
    fun setup(){
        authMock.startMockAuth()
    }

    @AfterAll
    fun teardown() {
        authMock.stopAuthMock()
    }

    @Nested
    inner class postServiceEndpoint {
        @Test
        fun `expect post to return 401 for unauthenticated users`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                                    mapServiceToJson("uniqeservice"))

            val status = result.getValue("status")
            expect(status).to_equal("401")
        }

        /** TODO get non-admin token**/
        @Test
        fun `expect post to return 403 for non-admin users`() {

            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                                    SERVICE_ENDPOINT,
                                    mapServiceToJson("nonadmin"),
                                    NON_ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("403")
        }

        /** TODO get admin token**/
        @Test
        fun `expect post to return 201 response for correct request`() {
            val name = "newservice"
            val result = postWithWritePermission(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson(name),
                    ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("201")

            val headers = result.getValue("header")
            expect(headers).to_contain("Location")
            expect(headers).to_contain("\"http://nothing.org/${name}\"")

        }

        @Test
        fun `expect post to return 400 for request with empty body`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                                    token = ADMIN_TOKEN_TMP)

            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("400")
        }
        @Test

        fun `expect post to return 400 for request with missing uri`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson("missinguriservice",
                                      addUri = false),
                    token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("400")
        }
        @Test
        fun `expect post to return 400 for request with missing name`() {
            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson("missingnameservice",
                            addName = false),
                            token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("400")
        }

        @Test
        fun `expect post to return 409 for duplicate service name`() {

            val setup_result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson("duplicateservice"),
                    ADMIN_TOKEN_TMP)

            val setup_status = setup_result.getValue("status")
            assume_success(setup_status)

            val result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson("duplicateservice"),
                    token = ADMIN_TOKEN_TMP)
            val status = result.getValue("status")
            expect(status).to_equal("409")
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

    @Nested
    inner class getVersionForServiceEndpoints {

        @Test
        fun `expect a Version for existing service`() {
            val name = "version-service"

            val setup_result = simplePost(TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    SERVICE_ENDPOINT,
                    mapServiceToJson(name),
                    ADMIN_TOKEN_TMP)

            val setup_status = setup_result.getValue("status")
            assume_success(setup_status)

            /**TODO Get id of version object**/

            val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    "/serviceendpoints/11111/version"
            )
            val status = result.getValue("status") as String
            assume_implemented(status)

            val body = result.getValue("body") as LinkedHashMap<String,String>
            expect(status).to_equal("200")
            expect(body).to_contain("repositoryUrl")
            expect(body["repositoryUrl"]).to_contain(name)
            expect(body).to_contain("branchName")
            expect(body).to_contain("buildTime")
            expect(body).to_contain("sha")
            expect(body).to_contain("versionId")
        }


        @Test
        fun `expect get to return 404 for non-existing id`() {
            val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    "/serviceendpoints/11111/version"
            )

            val status = result.getValue("status") as String
            assume_implemented(status)
            expect(status).to_equal("404")
        }
    }


}