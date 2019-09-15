package no.template.repository

import no.template.model.ServiceEndpointDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface ServiceEndpointRepository : MongoRepository<ServiceEndpointDB, String> {
    fun findByNameLike(name: String): List<ServiceEndpointDB>
}
