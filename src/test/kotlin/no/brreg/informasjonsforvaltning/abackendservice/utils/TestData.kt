package no.brreg.informasjonsforvaltning.abackendservice.utils

import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.Version
import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB
import java.net.URI
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer.Companion.TEST_API
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap


const val API_PORT = 8080
const val LOCAL_SERVER_PORT = 5000


const val MONGO_SERVICE_NAME = "mongodb"
const val MONGO_PORT = 27017

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"

const val EXISTING_SERVICE = "a-db-service"
const val EXPECT_NAME_CONFLICT = "existing-url-trouble"
const val EXISTING_SERVICE_URL = "http://host.testcontainers.internal:5000/${EXISTING_SERVICE}"

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


const val SERVICE_ENDPOINT = "/serviceendpoints"
const val VERSION_API_ENDPOINT = "/version"

fun getApiAddress( endpoint: String ): String{
   return "http://${TEST_API.containerIpAddress}:${TEST_API.getMappedPort(API_PORT)}$endpoint"
}


fun createServiceEndpointDB(testName: String,testUrl: String) =
    ServiceEndpointDB().apply {
        name = testName
        url = URL(testUrl)
    }

fun createServiceEndpoint(testName: String,testUrl: String) =
    ServiceEndpoint().apply {
        name = testName
        url = URI(testUrl)
    }

fun dataForPopulate(): org.bson.Document =
        org.bson.Document()
                .append("_id", EXISTING_SERVICE)
                .append("url", EXISTING_SERVICE_URL)

fun jsonServiceEndpointObject (name: String, addName: Boolean = true, addUri: Boolean = true ): String? {
    val map = mapOf<String,String?>(
            "name" to if(addName) name else null ,
            "url" to if(addUri) "http://nothing.org/${name}" else null
    )
    return jacksonObjectMapper().writeValueAsString(map)
}
fun jsonServiceDuplicateObject (): String? {
    val map = mapOf<String,String?>(
            "name" to EXPECT_NAME_CONFLICT,
            "url" to EXISTING_SERVICE_URL
    )
    return jacksonObjectMapper().writeValueAsString(map)
}

val EMPTY_DB_LIST = emptyList<ServiceEndpointDB>()
val ENDPOINTS_DB_LIST = listOf(createServiceEndpointDB(TestNames.CORRECT,TestUrls.CORRECT))


val VERSION_DATA = Version().apply{
    repositoryUrl = "repositoryUrl"
    branchName = "branchName"
    buildTime = "buildTime"
    sha = "sha"
    versionId = "versionId"
}

val CONTRACT_VERSION_DATA = Version().apply{
    repositoryUrl = "$EXISTING_SERVICE_URL.git"
    branchName = "master"
    buildTime = "54.775"
    sha = "sha"
    versionId = "1.0.4-SNAPSHOT"
}

val VERSION_JSON : String =
        jacksonObjectMapper().writeValueAsString(VERSION_DATA)

val CONTRACT_VERSION_JSON : String =
        jacksonObjectMapper().writeValueAsString(CONTRACT_VERSION_DATA)


object TestNames{
    const val CORRECT = "a-bakcend-service"
    const val WITH_WHITE_SPACE= "a backend service"
}

object TestUrls{
    const val CORRECT = "http://localhost:8080/version"
}

