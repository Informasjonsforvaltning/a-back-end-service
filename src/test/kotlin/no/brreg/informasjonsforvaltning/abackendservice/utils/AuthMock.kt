package no.brreg.informasjonsforvaltning.abackendservice.utils
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import no.brreg.informasjonsforvaltning.abackendservice.utils.jwk.JwkStore

private val mockserver = WireMockServer(LOCAL_SERVER_PORT)

fun startMockAuth() {

        mockserver.stubFor(get(urlEqualTo("/auth/realms/fdk/protocol/openid-connect/certs"))
                .willReturn(okJson(JwkStore.get() as String))
        )

        mockserver.stubFor(get(urlEqualTo("/ping"))
                .willReturn(aResponse()
                        .withStatus(200)))

        mockserver.start()

    }

fun stopAuthMock() {
    if (mockserver.isRunning) mockserver.stop()
}