package no.template.repository

import no.template.model.TemplateObjectDB
import org.springframework.data.mongodb.repository.MongoRepository
import org.springframework.stereotype.Repository

@Repository
interface TemplateRepository : MongoRepository<TemplateObjectDB, String> {
    fun findByNameLike(name: String): List<TemplateObjectDB>
}