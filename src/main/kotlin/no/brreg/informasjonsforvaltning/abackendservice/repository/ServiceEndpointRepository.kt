package no.brreg.informasjonsforvaltning.abackendservice.repository

import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceEndpointRepository : MongoRepository<ServiceEndpointDB, String> {
    fun findByNameLike(name: String): List<ServiceEndpointDB>
}
