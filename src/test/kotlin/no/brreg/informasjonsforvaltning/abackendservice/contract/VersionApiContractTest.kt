package no.brreg.informasjonsforvaltning.abackendservice.contract

import no.brreg.informasjonsforvaltning.abackendservice.utils.API_PORT
import no.brreg.informasjonsforvaltning.abackendservice.utils.API_SERVICE_NAME
import no.brreg.informasjonsforvaltning.abackendservice.utils.getContent
import no.brreg.informasjonsforvaltning.abackendservice.utils.AbstractDockerTestContainer as ApiContainer
import no.brreg.informasjonsforvaltning.abackendservice.utils.Expect as expect
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag


@Tag("service")
class VersionApiContractTest : ApiContainer() {

    @Nested
    inner class GetVersion {
        @Test
        fun `expect a version object`() {
            val result = getContent(ApiContainer.TEST_API.getServiceHost(API_SERVICE_NAME, API_PORT),
                    ApiContainer.TEST_API.getServicePort(API_SERVICE_NAME, API_PORT),
                    "/version"
            )
            val status = result.getValue("status") as String
            val body = result.getValue("body") as LinkedHashMap<*, *>

            expect(status).to_equal("200")
            expect(body).to_contain("repositoryUrl")
            expect(body["repositoryUrl"]).to_equal("https://github.com/Informasjonsforvaltning/a-back-end-service.git")
            expect(body).to_contain("branchName")
            expect(body).to_contain("buildTime")
            expect(body).to_contain("sha")
            expect(body).to_contain("versionId")
        }
    }

}
