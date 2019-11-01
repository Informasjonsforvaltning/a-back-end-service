package no.brreg.informasjonsforvaltning.abackendservice.utils.jwk

import com.nimbusds.jwt.JWTClaimsSet
import com.nimbusds.jwt.SignedJWT
import org.springframework.security.oauth2.provider.token.store.jwk.JwkTokenStore
import java.util.*
import kotlin.math.sign


object JwtToken {
    private val exp = Date().time + 120 * 1000
    private val aud = listOf<String>("a-backend-service")
    private var authorities: String? = null

    public fun buildRead(): String{
        authorities = access.ORG_READ
        return buildToken()
    }

    public fun buildWrite(): String{
        authorities = access.ORG_WRITE
        return buildToken()

    }

    public fun buildRoot() : String{
        authorities = access.ROOT
        return buildToken()
    }

    private fun buildToken() : String{
        val claimset = JWTClaimsSet.Builder()
                .audience(aud)
                .expirationTime(Date(exp))
                .claim("user_name","1924782563")
                .claim("name", "TEST USER")
                .claim("given_name", "TEST")
                .claim("family_name", "USER")
                .claim("authorities",authorities)
                .build()

        val signed = SignedJWT(JwkStore.jwtHeader(), claimset)
        signed.sign(JwkStore.signer())

        return signed.serialize()

    }

    private  object access{
        final val ORG_READ = "publisher:910244132:read"
        final val ORG_WRITE = "publisher:910244132:admin"
        final val ROOT = "system:root:admin"
    }
}

