package no.template.controller

import no.template.generated.model.TemplateObject
import javax.servlet.http.HttpServletRequest
import no.template.jena.MissingAcceptHeaderException
import no.template.jena.jenaResponse
import no.template.service.TemplateService
import org.slf4j.LoggerFactory
import org.springframework.dao.DuplicateKeyException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod.GET
import org.springframework.web.servlet.support.ServletUriComponentsBuilder
import javax.validation.ConstraintViolationException

private val LOGGER = LoggerFactory.getLogger(TemplateApiImpl::class.java)

@Controller
open class TemplateApiImpl (
    private val templateService: TemplateService
): no.template.generated.api.TemplateApi {

    @RequestMapping(value = ["/ping"], method = [GET], produces = ["text/plain"])
    fun ping(): ResponseEntity<String> =
        ResponseEntity.ok("pong")

    @RequestMapping(value = ["/ready"], method = [GET])
    fun ready(): ResponseEntity<Void> =
        ResponseEntity.ok().build()

    override fun createTemplateObject(httpServletRequest: HttpServletRequest, templateObject: TemplateObject): ResponseEntity<Void> =
        try {
            HttpHeaders()
                .apply {
                    location = ServletUriComponentsBuilder
                        .fromCurrentServletMapping()
                        .path("/template/{id}")
                        .build()
                        .expand(templateService.createTemplateObject(templateObject).id)
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

    override fun updateTemplateObject(httpServletRequest: HttpServletRequest, id: String, templateObject: TemplateObject): ResponseEntity<TemplateObject> =
        try {
            templateService
                .updateTemplateObject(id, templateObject)
                ?.let { updated -> ResponseEntity(updated, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("updateTemplateObject failed:", exception)
            when(exception) {
                is ConstraintViolationException -> ResponseEntity(HttpStatus.BAD_REQUEST)
                is DuplicateKeyException -> ResponseEntity(HttpStatus.CONFLICT)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getTemplateObjectById(httpServletRequest: HttpServletRequest, id: String): ResponseEntity<String> =
        try {
            templateService
                .getById(id)
                ?.let { templateObject -> templateObject.jenaResponse(httpServletRequest.getHeader("Accept")) }
                ?.let { response -> ResponseEntity(response, HttpStatus.OK) }
                ?: ResponseEntity(HttpStatus.NOT_FOUND)
        } catch (exception: Exception) {
            LOGGER.error("getTemplateObjectById failed:", exception)
            when(exception) {
                is MissingAcceptHeaderException -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }

    override fun getTemplateObjects(httpServletRequest: HttpServletRequest, name: String?): ResponseEntity<String> =
        try {
            templateService
                .getTemplateObjects(name)
                .let { templateObjects -> templateObjects.jenaResponse(httpServletRequest.getHeader("Accept")) }
                .let { response -> ResponseEntity(response, HttpStatus.OK) }
        } catch (exception: Exception) {
            LOGGER.error("getTemplateObjects failed:", exception)
            when(exception) {
                is MissingAcceptHeaderException -> ResponseEntity(HttpStatus.NOT_ACCEPTABLE)
                else -> ResponseEntity(HttpStatus.INTERNAL_SERVER_ERROR)
            }
        }
}
