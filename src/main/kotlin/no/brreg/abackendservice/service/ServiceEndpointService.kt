package no.brreg.abackendservice.service

import no.brreg.abackendservice.adapter.VersionAdapter
import no.brreg.abackendservice.generated.model.ServiceEndpoint
import no.brreg.abackendservice.generated.model.ServiceEndpointCollection
import no.brreg.abackendservice.mapping.mapForCreation
import no.brreg.abackendservice.mapping.mapToGenerated
import no.brreg.abackendservice.repository.ServiceEndpointRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

private val LOGGER = LoggerFactory.getLogger(ServiceEndpointService::class.java)

@Service
class ServiceEndpointService (
    private val serviceEndpointRepository: ServiceEndpointRepository,
    private val adapter: VersionAdapter
) {

    fun getServiceEndpoints(): ServiceEndpointCollection =
        serviceEndpointRepository
            .findAll()
            .map { it.mapToGenerated() }
            .map { it.apply { version = adapter.getVersionData(uri.toURL()) } }
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
