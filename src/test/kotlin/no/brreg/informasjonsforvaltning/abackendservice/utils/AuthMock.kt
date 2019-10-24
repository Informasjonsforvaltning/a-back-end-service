package no.brreg.informasjonsforvaltning.abackendservice.utils
import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock.*
import com.github.tomakehurst.wiremock.client.WireMock





    private val port = LOCAL_SERVER_PORT
    private val mockserver = WireMockServer(port)

    fun startMockAuth (){
        mockserver.stubFor(get(urlEqualTo("/auth/auth/realms/fdk/protocol/openid-connect/certs"))
                .willReturn(okJson("{\n" +
                        " \"keys\": [\n" +
                        "   {\n" +
                        "     \"kid\": \"MfFp7IWWRkFW3Yvhb1eVrtyQQNYqk6BG-6HZFpl_JxI\",\n" +
                        "     \"kty\": \"RSA\",\n" +
                        "     \"alg\": \"RS256\",\n" +
                        "     \"use\": \"sig\",\n" +
                        "     \"n\": \"qDWXUhNtfuHNh0lm3o-oTnP5S8ENpzsyi-dGrjSeewxV6GNiKTW5INJ4hDQ7ZWkUFfJJhfhQWJofqgN9rUBQgbRxXuUvEkrzXQiT9AT_8r-2XLMwRV3eV_t-WRIJhVWsm9CHS2gzbqbNP8HFoB_ZaEt2FYegQSoAFC1EXMioarQbFs7wFNEs1sn1di2xAjoy0rFrqf_UcYFNPlUhu7FiyhRrnoctAuQepV3B9_YQpFVoiUqa_p5THcDMaUIFXZmGXNftf1zlepbscaeoCqtiWTZLQHNuYKG4haFuJE4t19YhAZkPiqnatOUJv5ummc6i6CD69Mm9xAzYyMQUEvJuFw\",\n" +
                        "     \"e\": \"AQAB\"\n" +
                        "   }\n" +
                        " ]\n" +
                        "}"))
        )

        mockserver.stubFor(get(urlEqualTo("/check"))
                .willReturn(aResponse()
                        .withStatus(200)))

        mockserver.start()

    }

    fun stopAuthMock() {
        mockserver.verify(getRequestedFor(urlEqualTo("/auth/realms/fdk/protocol/openid-connect/certs")))
        mockserver.verify(getRequestedFor(urlEqualTo("/check")))
        if (mockserver.isRunning) {
            mockserver.stop()
        }
    }


/*
{
 "keys": [
   {
     "kid": "MfFp7IWWRkFW3Yvhb1eVrtyQQNYqk6BG-6HZFpl_JxI",
     "kty": "RSA",
     "alg": "RS256",
     "use": "sig",
     "n": "qDWXUhNtfuHNh0lm3o-oTnP5S8ENpzsyi-dGrjSeewxV6GNiKTW5INJ4hDQ7ZWkUFfJJhfhQWJofqgN9rUBQgbRxXuUvEkrzXQiT9AT_8r-2XLMwRV3eV_t-WRIJhVWsm9CHS2gzbqbNP8HFoB_ZaEt2FYegQSoAFC1EXMioarQbFs7wFNEs1sn1di2xAjoy0rFrqf_UcYFNPlUhu7FiyhRrnoctAuQepV3B9_YQpFVoiUqa_p5THcDMaUIFXZmGXNftf1zlepbscaeoCqtiWTZLQHNuYKG4haFuJE4t19YhAZkPiqnatOUJv5ummc6i6CD69Mm9xAzYyMQUEvJuFw",
     "e": "AQAB"
   }
 ]
}
 */
