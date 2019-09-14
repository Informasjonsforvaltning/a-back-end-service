package no.template.controller

import no.template.generated.model.ServiceEndpointCollection
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.*


@Controller
open class ServiceEndpointsApiImpl : no.template.generated.api.ServiceEndpointsApi {

    private var properties : Properties = Properties()


    override fun getServiceEndpoints(httpServletRequest: HttpServletRequest?): ResponseEntity<ServiceEndpointCollection> {
        if (properties.isEmpty) {
            properties.load(this::class.java.getResourceAsStream("/git.properties"))
        }

        return ServiceEndpointCollection().apply {
            total = 0
        }.let { response -> ResponseEntity(response, HttpStatus.OK) }
    }

    private fun getRepositoryName(remoteOriginUrl: String): String {
      return remoteOriginUrl.split("/").last().split(".").first()
    }

}
