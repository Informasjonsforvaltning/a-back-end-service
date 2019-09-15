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
import java.util.*

private val LOGGER = LoggerFactory.getLogger(ServiceEndpointsApiImpl::class.java)

@Controller
open class ServiceEndpointsApiImpl (
  private val serviceEndpointService: ServiceEndpointService
  ) : no.template.generated.api.ServiceEndpointsApi {

  private var properties : Properties = Properties()


  override fun getServiceEndpoints(httpServletRequest: HttpServletRequest?): ResponseEntity<ServiceEndpointCollection> {
    if (properties.isEmpty) {
      properties.load(this::class.java.getResourceAsStream("/git.properties"))
    }

    return ServiceEndpointCollection().apply {
      total = 0
      }.let { response -> ResponseEntity(response, HttpStatus.OK) }
    }

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
          LOGGER.error("createTemplateObject failed:", exception)
          when(exception) {
            is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
            is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
            else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
          }
        }

  private fun getRepositoryName(remoteOriginUrl: String): String {
    return remoteOriginUrl.split("/").last().split(".").first()
  }
}
