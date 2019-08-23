package no.template.jena

import no.template.generated.model.TemplateObject
import org.apache.jena.rdf.model.Model
import org.apache.jena.rdf.model.ModelFactory
import org.apache.jena.rdf.model.Property
import org.apache.jena.rdf.model.Resource
import org.apache.jena.sparql.vocabulary.FOAF
import org.apache.jena.vocabulary.DCTerms
import org.apache.jena.vocabulary.RDF
import org.apache.jena.vocabulary.ROV
import java.io.ByteArrayOutputStream

fun TemplateObject.jenaResponse(acceptHeader: String?): String =
    listOf(this)
        .createModel()
        .createResponseString(acceptHeaderToJenaType(acceptHeader))

fun List<TemplateObject>.jenaResponse(acceptHeader: String?): String =
    createModel()
        .createResponseString(acceptHeaderToJenaType(acceptHeader))

private fun List<TemplateObject>.createModel(): Model {
    val model = ModelFactory.createDefaultModel()
    model.setNsPrefix("dct", DCTerms.getURI())
    model.setNsPrefix("rov", ROV.getURI())

    forEach {
        model.createResource(it.uri)
            .addProperty(RDF.type, FOAF.Organization)
            .addProperty(DCTerms.identifier, it.id)
            .safeAddProperty(ROV.legalName, it.name)
            .safeAddLinkedProperty(DCTerms.isPartOf, it.uri)
    }

    return model
}

private fun Resource.safeAddProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, value)

private fun Resource.safeAddLinkedProperty(property: Property, value: String?): Resource =
    if(value == null) this
    else addProperty(property, model.createResource(value))

private fun Model.createResponseString(responseType: String):String =
    ByteArrayOutputStream().use{ out ->
        write(out, responseType)
        out.flush()
        out.toString("UTF-8")
    }

private fun acceptHeaderToJenaType(accept: String?): String =
    when (accept) {
        "text/turtle" -> "TURTLE"
        "application/rdf+xml" -> "RDF/XML"
        "application/ld+json" -> "JSON-LD"
        else -> throw MissingAcceptHeaderException()
    }

class MissingAcceptHeaderException(): Exception()