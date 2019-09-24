package no.template.service

import no.template.adapter.VersionAdapter
import no.template.generated.model.ServiceEndpoint
import no.template.generated.model.ServiceEndpointCollection
import no.template.mapping.mapForCreation
import no.template.mapping.mapToGenerated
import no.template.repository.ServiceEndpointRepository
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
            .map { it.apply { version = adapter.getVersionData(uri) } }
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
