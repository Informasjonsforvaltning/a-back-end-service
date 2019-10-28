package no.brreg.informasjonsforvaltning.abackendservice.service

import no.brreg.informasjonsforvaltning.abackendservice.adapter.VersionAdapter
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpointCollection
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.Version
import no.brreg.informasjonsforvaltning.abackendservice.mapping.getVersionURl
import no.brreg.informasjonsforvaltning.abackendservice.mapping.mapForCreation
import no.brreg.informasjonsforvaltning.abackendservice.mapping.mapToGenerated
import no.brreg.informasjonsforvaltning.abackendservice.repository.ServiceEndpointRepository
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
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
            .let {
                ServiceEndpointCollection().apply {
                    total = it.size
                    serviceEndpoints = it
                } }

    fun createServiceEndpoint(serviceEndpoint: ServiceEndpoint): ServiceEndpoint =
        serviceEndpointRepository
            .save(serviceEndpoint.mapForCreation())
            .mapToGenerated()

    fun getVersionData(id: String): Version? =
        serviceEndpointRepository.findByIdOrNull(id)
            ?.getVersionURl()
            ?.let { adapter.getVersionData(it) }
}
