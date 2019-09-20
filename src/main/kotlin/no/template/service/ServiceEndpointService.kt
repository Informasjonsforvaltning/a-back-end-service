package no.template.service

import no.template.generated.model.ServiceEndpoint
import no.template.generated.model.ServiceEndpointCollection
import no.template.mapping.mapForCreation
import no.template.mapping.mapToGenerated
import no.template.repository.ServiceEndpointRepository
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class ServiceEndpointService (
    private val serviceEndpointRepository: ServiceEndpointRepository
) {

    fun getServiceEndpoints(): ServiceEndpointCollection =
        serviceEndpointRepository
            .findAll()
            .map { it.mapToGenerated() }
            .let {
                ServiceEndpointCollection().apply {
                    total = it.size
                    serviceEndpoints = it
                } }

    fun createServiceEndpoint(serviceEndpoint: ServiceEndpoint): ServiceEndpoint =
        serviceEndpointRepository
            .save(serviceEndpoint.mapForCreation())
            .mapToGenerated()

}
