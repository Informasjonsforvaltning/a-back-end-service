package no.brreg.informasjonsforvaltning.abackendservice.controller

import no.brreg.informasjonsforvaltning.abackendservice.API_PORT
import no.brreg.informasjonsforvaltning.abackendservice.API_SERVICE_NAME
import no.brreg.informasjonsforvaltning.abackendservice.getContent
import no.brreg.informasjonsforvaltning.abackendservice.integration.AbstractDockerTestContainer as ApiContainer
import no.brreg.informasjonsforvaltning.abackendservice.Expect as expect
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Timeout
import java.util.concurrent.TimeUnit


@Tag("service")
class VersionApiComponentTest : ApiContainer() {

    @Nested
    inner class ` GetVersion ` {
        @Test
        @Timeout(value=1, unit = TimeUnit.HOURS)
        fun `expect a version object`() {
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

}
