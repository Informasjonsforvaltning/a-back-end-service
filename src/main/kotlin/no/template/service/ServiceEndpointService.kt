package no.template.service

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.template.generated.model.ServiceEndpoint
import no.template.generated.model.ServiceEndpointCollection
import no.template.generated.model.Version
import no.template.mapping.mapForCreation
import no.template.mapping.mapToGenerated
import no.template.repository.ServiceEndpointRepository
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.net.URI

private val LOGGER = LoggerFactory.getLogger(ServiceEndpointService::class.java)

@Service
class ServiceEndpointService (
    private val serviceEndpointRepository: ServiceEndpointRepository
) {

    fun getServiceEndpoints(): ServiceEndpointCollection =
        serviceEndpointRepository
            .findAll()
            .map { it.mapToGenerated() }
            .map { it.apply { version = uri.getVersionData() } }
            .let {
                ServiceEndpointCollection().apply {
                    total = it.size
                    serviceEndpoints = it
                } }

    fun createServiceEndpoint(serviceEndpoint: ServiceEndpoint): ServiceEndpoint =
        serviceEndpointRepository
            .save(serviceEndpoint.mapForCreation())
            .mapToGenerated()

    private fun URI.getVersionData(): Version {
        val jsonBody = toURL().openConnection().inputStream.bufferedReader().use(BufferedReader::readText)
        return jacksonObjectMapper().readValue(jsonBody)
    }

}
