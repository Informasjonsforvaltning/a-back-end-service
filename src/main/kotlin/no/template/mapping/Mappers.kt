package no.template.mapping

import no.template.generated.model.ServiceEndpoint
import no.template.model.ServiceEndpointDB

fun ServiceEndpointDB.mapToGenerated(): ServiceEndpoint {
    val mapped = ServiceEndpoint()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri.toURI()

    return mapped
}

fun ServiceEndpoint.mapForCreation(): ServiceEndpointDB {
    val mapped = ServiceEndpointDB()

    mapped.name = name
    mapped.uri = uri.toURL()

    return mapped
}
