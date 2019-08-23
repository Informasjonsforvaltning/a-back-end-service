package no.template.service

import no.template.generated.model.TemplateObject
import no.template.mapping.mapForCreation
import no.template.mapping.mapToGenerated
import no.template.mapping.updateValues
import no.template.repository.TemplateRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class TemplateService (
    private val templateRepository: TemplateRepository
) {
    fun getById(id: String): TemplateObject? =
        templateRepository
            .findByIdOrNull(id)
            ?.mapToGenerated()

    fun getTemplateObjects(name: String?): List<TemplateObject> =
        when {
            name != null -> getTemplateObjectsByName(name)
            else -> getAllTemplateObjects()
        }

    private fun getAllTemplateObjects() =
        templateRepository
            .findAll()
            .map { it.mapToGenerated() }

    private fun getTemplateObjectsByName(name: String) =
        templateRepository
            .findByNameLike(name)
            .map { it.mapToGenerated() }

    fun createTemplateObject(templateObject: TemplateObject): TemplateObject =
        templateRepository
            .save(templateObject.mapForCreation())
            .mapToGenerated()

    fun updateTemplateObject(id: String, templateObject: TemplateObject): TemplateObject? =
        templateRepository
            .findByIdOrNull(id)
            ?.updateValues(templateObject)
            ?.let { templateRepository.save(it) }
            ?.mapToGenerated()
}