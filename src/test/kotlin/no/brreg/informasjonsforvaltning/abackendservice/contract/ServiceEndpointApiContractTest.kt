package no.brreg.informasjonsforvaltning.abackendservice.contract

import no.brreg.informasjonsforvaltning.abackendservice.utils.apiPost
import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer
import no.brreg.informasjonsforvaltning.abackendservice.utils.CONTRACT_VERSION_DATA
import no.brreg.informasjonsforvaltning.abackendservice.utils.EXISTING_SERVICE
import no.brreg.informasjonsforvaltning.abackendservice.utils.SERVICE_ENDPOINT
import no.brreg.informasjonsforvaltning.abackendservice.utils.apiGet
import no.brreg.informasjonsforvaltning.abackendservice.utils.assume_authenticated
import no.brreg.informasjonsforvaltning.abackendservice.utils.jsonServiceDuplicateObject
import no.brreg.informasjonsforvaltning.abackendservice.utils.jwk.JwtToken
import no.brreg.informasjonsforvaltning.abackendservice.utils.startMockServer
import no.brreg.informasjonsforvaltning.abackendservice.utils.jsonServiceEndpointObject as mapServiceToJson
import no.brreg.informasjonsforvaltning.abackendservice.utils.Expect as expect
import org.junit.jupiter.api.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.stopMockServer


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("contract")
class ServiceEndpointApiContractTest : ApiTestContainer() {

    val adminToken = JwtToken.buildRoot()
    var nonAdminToken = JwtToken.buildRead()

    @BeforeAll
    fun setup() {
        startMockServer()
    }

    @AfterAll
    fun teardown() {
        stopMockServer()
    }

    @Nested
    inner class PostServiceEndpoint {
        @Test
        fun `expect post to return 401 for user withouth token`() {
            val result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson("uniqeservice"),
                    token = null
            )
            val status = result.getValue("status")
            expect(status).to_equal("401")
        }

        @Test
        fun `expect post to return 403 for non-admin users`() {

            val result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson("different-service"),
                    token = nonAdminToken
            )

            val status = result.getValue("status")

            expect(status).to_equal("403")
        }

        @Test
        fun `expect post to return 201 response for correct request`() {

            val result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson("correct-service"),
                    token = adminToken
            )

            val status = result.getValue("status")
            expect(status).to_equal("201")

            /* TODO : implement location header
            val headers = result.getValue("header")
            expect(headers).to_contain("Location")
            expect(headers).to_contain("\"http://nothing.org/${name}\"")
            */
        }

        @Test
        fun `expect post to return 400 for request with empty body`() {

            val result = apiPost(
                    SERVICE_ENDPOINT,
                    body = null,
                    token = adminToken
            )

            val status = result.getValue("status")
            expect(status).to_equal("400")
        }

        @Test
        fun `expect post to return 400 for request with missing uri`() {
            val result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson("missinguriservice", addUri = false),
                    token = adminToken
            )

            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("400")
        }

        @Test
        fun `expect post to return 400 for request with missing name`() {
            val result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson("missingnameservice",
                            addName = false),
                    token = adminToken
            )

            val status = result.getValue("status")
            assume_authenticated(status)

            expect(status).to_equal("400")
        }

        @Test
        fun `expect post to return 409 for duplicate service name`() {

            val result = apiPost(
                    SERVICE_ENDPOINT,
                    jsonServiceDuplicateObject(),
                    token = adminToken
            )

            val status = result.getValue("status")
            expect(status).to_equal("409")
        }

    }

    @Nested
    inner class GetServiceEndpoints {
        @Test
        fun `expect a ServiceEndpointCollection`() {
            val result = apiGet(SERVICE_ENDPOINT)

            val body = result.getValue("body")
            expect(body).to_contain("total")
            expect(body).to_contain("serviceEndpoints")
        }
    }


    @Nested
    inner class GetVersionForServiceEndpoints {

        @Test
        fun `expect a Version for existing service`() {

            val result = apiGet("$SERVICE_ENDPOINT/$EXISTING_SERVICE/version")

            val status = result.getValue("status" as String)
            expect(status).to_equal(200 )

            val body = result.getValue( "body") as LinkedHashMap<String,String>

            expect(body["repositoryUrl"]).to_equal(CONTRACT_VERSION_DATA.repositoryUrl)
            expect(body["buildTime"]).to_equal(CONTRACT_VERSION_DATA.buildTime)
            expect(body["branchName"]).to_equal(CONTRACT_VERSION_DATA.branchName)
            expect(body["versionId"]).to_equal(CONTRACT_VERSION_DATA.versionId)
        }


        @Test
        fun `expect get to return 404 for non-existing id`() {
            val result = apiGet("/serviceendpoints/notfound/version")

            val status = result.getValue("status") as Int
            expect(status).to_equal(404)
        }
    }
}
