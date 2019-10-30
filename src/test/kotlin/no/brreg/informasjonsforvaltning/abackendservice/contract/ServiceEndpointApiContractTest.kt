package no.brreg.informasjonsforvaltning.abackendservice.contract

import no.brreg.informasjonsforvaltning.abackendservice.utils.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.apiPost
import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer
import no.brreg.informasjonsforvaltning.abackendservice.utils.jwk.JwtToken
import no.brreg.informasjonsforvaltning.abackendservice.utils.jsonServiceEndpointObject as mapServiceToJson
import no.brreg.informasjonsforvaltning.abackendservice.utils.Expect as expect
import org.junit.jupiter.api.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.stopAuthMock


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("service")
class ServiceEndpointApiContractTest : ApiTestContainer(){

    //Placeholders
    val adminToken = JwtToken.buildRoot()
    var nonAdminToken = JwtToken.buildRead()

    @AfterAll
    fun teardown() {
        stopAuthMock()
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
                    mapServiceToJson("uniqeservice"),
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

            /* TODO : implement endpoint for getting serviceendpoint object
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

            //TODO: populate db with existing service

            val setup_result = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson(EXISTING_SERVICE),
                    token = adminToken
            )

            val setup_status = setup_result.getValue("status")
            assume_success(setup_status)

            val result = apiPost(
                    SERVICE_ENDPOINT,
                    jsonServiceDuplicateObject(EXISTING_SERVICE),
                    token = adminToken
            )

            val status = result.getValue("status")
            expect(status).to_equal("409")
        }

    }

    @Nested
    inner class getServiceEndpoints {
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
            /**TODO populate DB, use name from service in db*/
            val name = "some-new-service"


            val setup = apiPost(
                    SERVICE_ENDPOINT,
                    mapServiceToJson(name),
                    token = adminToken
            )

            val sResult = setup.getValue("status")
            assume_success(sResult)

            val result = apiGet("/some-new-service/version")
            val status = result.getValue("status") as String

            /*

            val body = result.getValue("body") as LinkedHashMap<String,String>
            expect(status).to_equal("200")
            expect(body).to_contain("repositoryUrl")
            expect(body["repositoryUrl"]).to_contain(name)
            expect(body).to_contain("branchName")
            expect(body).to_contain("buildTime")
            expect(body).to_contain("sha")
            expect(body).to_contain("versionId")*/

        }


        @Test
        fun `expect get to return 404 for non-existing id`() {
            val result = apiGet("/serviceendpoints/notfound/version")
        /*
            val status = result.getValue("status") as String
            expect(status).to_equal("404")*/
        }
    }
}
