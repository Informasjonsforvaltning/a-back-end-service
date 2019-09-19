package no.template.controller

import no.template.generated.model.ServiceEndpointCollection
import no.template.generated.model.ServiceEndpoint
import no.template.service.ServiceEndpointService
import org.slf4j.LoggerFactory
import javax.servlet.http.HttpServletRequest
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(ServiceEndpointApiImpl::class.java)

@Controller
open class ServiceEndpointApiImpl (
  private val serviceEndpointService: ServiceEndpointService
) : no.template.generated.api.ServiceEndpointApi {

  override fun getServiceEndpoints(httpServletRequest: HttpServletRequest?): ResponseEntity<ServiceEndpointCollection> =
    ServiceEndpointCollection()
      .apply { total = 0 }
      .let { response -> ResponseEntity(response, HttpStatus.OK) }

  override fun createServiceEndpoint(httpServletRequest: HttpServletRequest, serviceEndpoint: ServiceEndpoint): ResponseEntity<Void> =
    try {
      HttpHeaders()
        .apply {
          location = ServletUriComponentsBuilder
            .fromCurrentServletMapping()
            .path("/serviceendpoint/{id}")
            .build()
            .expand(serviceEndpointService.createServiceEndpoint(serviceEndpoint).id)
            .toUri() }
        .let { ResponseEntity(it, HttpStatus.CREATED) }
    } catch (exception: Exception) {
      LOGGER.error("createServiceEndpoint failed:", exception)
      when(exception) {
        is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
        is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
        else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
      }
    }
}
