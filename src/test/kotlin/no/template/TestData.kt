package no.template

import com.google.common.collect.ImmutableMap
import no.template.generated.model.ServiceEndpoint
import no.template.model.ServiceEndpointDB
import org.bson.types.ObjectId
import java.net.URI

private const val MONGO_USER = "testuser"
private const val MONGO_PASSWORD = "testpassword"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"
const val MONGO_PORT = 27017
const val DATABASE_NAME = "templateAPI"

val GENERATED_ID_0 = ObjectId("5d846c475f599c04093216c3")

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
    "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
    "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD)

fun buildMongoURI(host: String, port: Int): String =
    "mongodb://$MONGO_USER:$MONGO_PASSWORD@$host:$port/$MONGO_AUTH"

fun createServiceEndpointDB(objectId: ObjectId?) =
    ServiceEndpointDB().apply {
        id = objectId
        name = "Endpoint name"
        uri = URI("https://endpoint.uri.no/version")
    }

fun createServiceEndpoint(hexStringId: String?) =
    ServiceEndpoint().apply {
        id = hexStringId
        name = "Endpoint name"
        uri = URI("https://endpoint.uri.no/version")
    }