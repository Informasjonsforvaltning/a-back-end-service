package no.template.mapping

import no.template.generated.model.TemplateObject
import no.template.model.TemplateObjectDB

fun TemplateObjectDB.mapToGenerated(): TemplateObject {
    val mapped = TemplateObject()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri

    return mapped
}

fun TemplateObject.mapForCreation(): TemplateObjectDB {
    val mapped = TemplateObjectDB()

    mapped.name = name
    mapped.uri = uri

    return mapped
}

fun TemplateObjectDB.updateValues(templateObject: TemplateObject): TemplateObjectDB =
    apply {
        name = templateObject.name ?: name
        uri = templateObject.uri ?: uri
    }
