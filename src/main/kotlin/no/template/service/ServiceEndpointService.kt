package no.template.service

import no.template.generated.model.ServiceEndpoint
import no.template.mapping.mapForCreation
import no.template.mapping.mapToGenerated
import no.template.repository.ServiceEndpointRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ServiceEndpointService (
    private val serviceEndpointRepository: ServiceEndpointRepository
) {

    fun createServiceEndpoint(serviceEndpoint: ServiceEndpoint): ServiceEndpoint =
        serviceEndpointRepository
            .save(serviceEndpoint.mapForCreation())
            .mapToGenerated()

}
