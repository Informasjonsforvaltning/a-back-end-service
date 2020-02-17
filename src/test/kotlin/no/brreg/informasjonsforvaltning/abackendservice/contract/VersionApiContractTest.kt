package no.brreg.informasjonsforvaltning.abackendservice.contract

import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer
import no.brreg.informasjonsforvaltning.abackendservice.utils.VERSION_API_ENDPOINT
import no.brreg.informasjonsforvaltning.abackendservice.utils.apiGet
import no.brreg.informasjonsforvaltning.abackendservice.utils.Expect as expect
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.TestInstance

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("contract")
class VersionApiContractTest : ApiTestContainer() {

    @Nested
    inner class GetVersion {
        @Test
        fun `expect a version object`() {
            val result = apiGet(VERSION_API_ENDPOINT)

            val status = result.getValue("status").toString()
            val body = result.getValue("body") as LinkedHashMap<*, *>

            expect(status).to_equal("200")
            expect(body).to_contain("repositoryUrl")
            //TODO get full path when test is run with github actions (then change test to to_equal ("https://github.com/Informasjonsforvaltning/a-back-end-service.git)"
            //expect(body["repositoryUrl"]).to_equal("https://github.com/Informasjonsforvaltning/a-back-end-service")
            expect(body).to_contain("branchName")
            expect(body).to_contain("buildTime")
            expect(body).to_contain("sha")
            expect(body).to_contain("versionId")
        }
    }

}
