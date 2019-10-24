package no.brreg.informasjonsforvaltning.abackendservice.utils

import no.brreg.informasjonsforvaltning.abackendservice.generated.model.ServiceEndpoint
import no.brreg.informasjonsforvaltning.abackendservice.generated.model.Version
import no.brreg.informasjonsforvaltning.abackendservice.model.ServiceEndpointDB
import org.bson.types.ObjectId
import java.net.URI
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper

const val API_SERVICE_NAME = "a-backend-service"
const val API_PORT = 8080

const val MONGO_SERVICE_NAME = "mongodb"
const val MONGO_PORT = 27017

const val MONGO_USER = "testuser"
const val MONGO_PASSWORD = "testpassword"

const val LOCAL_SERVER_PORT = 5000

val GENERATED_ID_0 = ObjectId("5d846c475f599c04093216c3")

const val DATABASE_NAME = "a-backend-service"
const val MONGO_COLLECTION = "service-endpoints"
private const val MONGO_AUTH = "?authSource=admin&authMechanism=SCRAM-SHA-1"

const val ADMIN_TOKEN ="eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYYkJCSlU5dnlDbVZEeFBqV1pmY0V5NzBaSVFrWks2eFYybHhuT09wcVVrIn0.eyJqdGkiOiJjODFkYzExNi02Yzg1LTQ0MGMtOTZkYi0yNjU1Nzg1NzU0ZjciLCJleHAiOjE1NzE3MzA5MzgsIm5iZiI6MCwiaWF0IjoxNTcxNzMwNjM4LCJpc3MiOiJodHRwczovL3Nzby51dDEuZmVsbGVzZGF0YWthdGFsb2cuYnJyZWcubm8vYXV0aC9yZWFsbXMvZmRrIiwiYXVkIjpbImEtYmFja2VuZC1zZXJ2aWNlIiwiZmRrLWhhcnZlc3QtYWRtaW4iLCJvcmdhbml6YXRpb24tY2F0YWxvZ3VlIiwiYWNjb3VudCJdLCJzdWIiOiJmOmFiMTI2ZmU5LTlmZmEtNDE2OS1iZDM5LTFmYzgwMjFiZmQwODoyMzA3NjEwMjI1MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZkay1hZG1pbi1ndWkiLCJhdXRoX3RpbWUiOjE1NzE3MzA2MzcsInNlc3Npb25fc3RhdGUiOiJhNDNhZDIzOC1iNzI5LTQyOGItYjNhYi03MGViNWIwZDM3OGEiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYWRtaW4udXQxLmZlbGxlc2RhdGFrYXRhbG9nLmJycmVnLm5vIiwiaHR0cDovL2xvY2FsaG9zdDo4MTM3Il0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwidXNlcl9uYW1lIjoiMjMwNzYxMDIyNTIiLCJuYW1lIjoiTUFVRCBHVUxMSUtTRU4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiIyMzA3NjEwMjI1MiIsImdpdmVuX25hbWUiOiJNQVVEIiwiZmFtaWx5X25hbWUiOiJHVUxMSUtTRU4iLCJhdXRob3JpdGllcyI6InB1Ymxpc2hlcjo5MTAyNDQxMzI6YWRtaW4sc3lzdGVtOnJvb3Q6YWRtaW4ifQ.UbXf00TILi0Qia7xQhuMEi6ONcclmikdyo2wtrXhujL0L2nB8_MC0S0Lyakfm6ZysNRtSw3LPMMHUJypIR85bnmnxVkZrJf0E5d6A81OuXNW7xG9cFHQRM-NmrLAfRwQN7m50fbiG7wuk3oRsIHl2SYc1kfUXBTK3LHJ2EIi9FeCdUtAhKup1scgXNkOciUWp7w80pSI6bZvD9FxnFW5dqaA5F6TPQS-oiQsDQuP1WmtJ5a4_cUYtLNZRa7XId09QONarkNf0VIbybLPdiprvXedM7XK66BX4YffrW7UmX-F-7JJguFh1tfbqsLry5ohSVEV5r1ymAWq0N4Zwd4cGA"
const val NON_ADMIN_TOKEN_TMP ="dfghjklasfijkf5fgahfskjl"

const val SERVICE_ENDPOINT = "/serviceendpoints"

fun mongoUri(host: String, port: Int): String =
    "mongodb://$MONGO_USER:$MONGO_PASSWORD@$host:$port/$DATABASE_NAME$MONGO_AUTH"

fun createServiceEndpointDB(testName: String, testUrl: String) =
    ServiceEndpointDB().apply {
        name = testName
        url = URL(testUrl)
    }

fun createServiceEndpoint(testName: String,testUrl: String) =
    ServiceEndpoint().apply {
        name = testName
        url = URI(testUrl)
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
        url = URI("http://localhost:$API_PORT/version")
    }

val EMPTY_DB_LIST = emptyList<ServiceEndpointDB>()
val ENDPOINTS_DB_LIST = listOf(createServiceEndpointDB(TestNames.CORRECT,TestUrls.CORRECT))

val EMPTY_ENDPOINTS_LIST = emptyList<ServiceEndpoint>()
val ENDPOINTS_LIST = listOf(createServiceEndpointWithVersionData(GENERATED_ID_0.toHexString()))


val VERSION_DATA = Version().apply{
    repositoryUrl = "repositoryUrl"
    branchName = "branchName"
    buildTime = "buildTime"
    sha = "sha"
    versionId = "versionId"
}

val VERSION_JSON = (
        jacksonObjectMapper().writeValueAsString(VERSION_DATA))
object TestNames{
    val CORRECT = "a-bakcend-service"
    val WITH_WHITE_SPACE= "a backend service"
    val WITH_NUMBER = "a-back3nd-servi3"
}

object TestUrls{
    val CORRECT = "http://localhost:8080/version"
    val NOT_URL = "this is not a url"
    val INVALID_FORMAT = "http:8080/version"
}

