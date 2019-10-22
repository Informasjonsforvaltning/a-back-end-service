package no.brreg.informasjonsforvaltning.abackendservice.utils
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*


class AuthMock {
    private val port = 8080
    private val mockserver = WireMockServer(port)

    //http://sso:8084/auth/realms/fdk/protocol/openid-connect/certs

    fun startMockAuth (){
        mockserver.start()
        mockserver.stubFor(get(urlEqualTo("/auth")).atPriority(1)
                .willReturn(aResponse().
                        withStatus(200)));
    }

    fun stopAuthMock() {
        mockserver.stop()
    }
}