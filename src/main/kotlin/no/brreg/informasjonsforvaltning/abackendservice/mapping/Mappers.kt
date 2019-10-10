package no.brreg.informasjonsforvaltning.abackendservice.mapping

import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB

fun ServiceEndpointDB.mapToGenerated(): ServiceEndpoint {
    val mapped = ServiceEndpoint()

    mapped.id = id.toHexString()
    mapped.name = name
    mapped.uri = uri

    return mapped
}

fun ServiceEndpoint.mapForCreation(): ServiceEndpointDB {
    val mapped = ServiceEndpointDB()

    mapped.name = name
    mapped.uri = uri

    return mapped
}
