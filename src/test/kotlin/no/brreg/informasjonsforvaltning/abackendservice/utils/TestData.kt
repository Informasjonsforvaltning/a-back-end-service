package no.brreg.informasjonsforvaltning.abackendservice.utils

import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.Version
import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB
import org.bson.types.ObjectId
import java.net.URI
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer.TEST_API
import org.apiguardian.api.API
import org.testcontainers.containers.GenericContainer
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap

const val API_SERVICE_NAME = "a-backend-service"
const val API_PORT = 8080

const val MONGO_SERVICE_NAME = "mongodb"
const val MONGO_PORT = 27017

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"

const val EXISTING_SERVICE="a-duplicate-service"

val MONGO_ENV_VALUES: Map<String, String> = ImmutableMap.of(
        "MONGO_INITDB_ROOT_USERNAME", MONGO_USER,
        "MONGO_INITDB_ROOT_PASSWORD", MONGO_PASSWORD
)

val API_ENV_VALUES : Map<String,String> = ImmutableMap.of(
        "MONGO_USERNAME", MONGO_USER,
        "MONGO_PASSWORD", MONGO_PASSWORD,
        "MONGO_HOST", "$MONGO_SERVICE_NAME:$MONGO_PORT",
        "SPRING_PROFILES_ACTIVE" , "test"
)


const val LOCAL_SERVER_PORT = 5000

val GENERATED_ID_0 = ObjectId("5d846c475f599c04093216c3")

const val DATABASE_NAME = "a-backend-service"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"

const val SERVICE_ENDPOINT = "/serviceendpoints"
const val VERSION_API_ENDPOINT = "/version"


fun mongoUri(host: String, port: Int): String =
    "mongodb://$MONGO_USER:$MONGO_PASSWORD@$host:$port/$DATABASE_NAME$MONGO_AUTH"

fun getApiAddress( endpoint: String ): String{
   return "http://${TEST_API.getContainerIpAddress()}:${TEST_API.getMappedPort(API_PORT)}$endpoint"
}


fun createServiceEndpointDB(objectId: ObjectId?) =
    ServiceEndpointDB().apply {
        id = objectId
        name = "Endpoint name"
        uri = URL("http://localhost:$API_PORT/version")
    }

fun createServiceEndpoint(hexStringId: String?) =
    ServiceEndpoint().apply {
        id = hexStringId
        name = "Endpoint name"
        uri = URI("http://localhost:$API_PORT/version")
    }

fun jsonServiceEndpointObject (name: String, addName: Boolean = true, addUri: Boolean = true ): String? {
    val map = mapOf<String,String?>(
            "name" to if(addName) name else null ,
            "uri" to if(addUri) "http://nothing.org/${name}" else null
    )
    return jacksonObjectMapper().writeValueAsString(map)
}

private fun createServiceEndpointWithVersionData(hexStringId: String?) =
    ServiceEndpoint().apply {
        id = hexStringId
        name = "Endpoint name"
        uri = URI("http://localhost:$API_PORT/version")
    }

val EMPTY_DB_LIST = emptyList<ServiceEndpointDB>()
val ENDPOINTS_DB_LIST = listOf(createServiceEndpointDB(GENERATED_ID_0))

val EMPTY_ENDPOINTS_LIST = emptyList<ServiceEndpoint>()
val ENDPOINTS_LIST = listOf(createServiceEndpointWithVersionData(GENERATED_ID_0.toHexString()))


val VERSION_DATA = Version().apply{
    repositoryUrl = "repositoryUrl"
    branchName = "branchName"
    buildTime = "buildTime"
    sha = "sha"
    versionId = "versionId"
}

