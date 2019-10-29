package no.brreg.informasjonsforvaltning.abackendservice.controller

import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpointCollection
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.Version
import no.brreg.informasjonsforvaltning.abackendservice.security.EndpointPermissions
import no.brreg.informasjonsforvaltning.abackendservice.service.ServiceEndpointService
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.net.MalformedURLException
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(ServiceEndpointApiImpl::class.java)

@Controller
open class ServiceEndpointApiImpl (
    private val endpointService: ServiceEndpointService,
    private val endpointPermissions: EndpointPermissions
) : no.brreg.informasjonsforvaltning.abackendservice.generated.api.ServiceEndpointApi {

    override fun getServiceEndpoints(httpServletRequest: HttpServletRequest): ResponseEntity<ServiceEndpointCollection> =
        ResponseEntity(endpointService.getServiceEndpoints(), HttpStatus.OK)

    override fun createServiceEndpoint(httpServletRequest: HttpServletRequest, serviceEndpoint: ServiceEndpoint): ResponseEntity<Void> =
        if (endpointPermissions.hasAdminPermission()) {
            try {
                endpointService.createServiceEndpoint(serviceEndpoint)
                ResponseEntity<Void>(HttpStatus.CREATED)
            } catch (exception: Exception) {
                LOGGER.error("createServiceEndpoint failed:", exception)
                when (exception){
                    is MalformedURLException -> ResponseEntity<Void>(HttpStatus.BAD_REQUEST)
                    is ConstraintViolationException -> ResponseEntity<Void>(HttpStatus.BAD_REQUEST)
                    is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                    else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
                }
            }
        } else ResponseEntity(HttpStatus.FORBIDDEN)

    override fun getVersionForServiceEndpoint(httpServletRequest: HttpServletRequest, id: String): ResponseEntity<Version> =
        endpointService
            .getVersionData(id)
            ?.let { ResponseEntity(it, HttpStatus.OK) }
            ?: ResponseEntity(HttpStatus.NOT_FOUND)
}
