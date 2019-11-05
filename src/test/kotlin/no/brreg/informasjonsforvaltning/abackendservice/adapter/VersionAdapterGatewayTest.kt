package no.brreg.informasjonsforvaltning.abackendservice.adapter

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.VERSION_DATA

import no.brreg.informasjonsforvaltning.abackendservice.utils.VERSION_JSON
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull

import java.net.URL

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Tag("unit")
class VersionAdapterGatewayTest {
    private val versionAdapter = VersionAdapter()
    private val port = 8080
    private val mockServer = WireMockServer(port)
    private val urlFoAdapter  = URL("http://localhost:${port}/version")

    @BeforeAll
    fun setup (){
        mockServer.start()
    }

    @Test
    fun `expect VersionAdapter to get version from a existing endpoint`(){
        val jsonString = VERSION_JSON
        mockServer.stubFor(
                get(urlEqualTo("/version"))
                    .willReturn(aResponse().withBody(jsonString))
        )

        val expeted = VERSION_DATA
        val version = versionAdapter.getVersionData(urlFoAdapter)

        assertEquals(expeted.branchName, version.branchName)
        assertEquals(expeted.buildTime, version.buildTime)
        assertEquals(expeted.repositoryUrl, version.repositoryUrl)
        assertEquals(expeted.sha, version.sha)

    }

    @AfterAll
    fun cleanup(){
        mockServer.stop()
    }

}