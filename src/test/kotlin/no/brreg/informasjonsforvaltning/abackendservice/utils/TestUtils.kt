package no.brreg.informasjonsforvaltning.abackendservice.utils

import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus


fun createTmpComposeFile(): File {
    val tmpFile = File.createTempFile("test-compose", ".yml")
    tmpFile.writeText("version: \"3.2\"\n" +
        "\n" +
        "services:\n" +
        "  a-backend-service:\n" +
        "    image: brreg/a-backend-service:latest\n" +
        "    environment:\n" +
        "      - MONGO_USERNAME=$MONGO_USER\n" +
        "      - MONGO_PASSWORD=$MONGO_PASSWORD\n" +
        "      - MONGO_HOST=mongodb:27017\n" +
        "    depends_on:\n" +
        "      - $MONGO_SERVICE_NAME\n" +
        "\n" +
        "  $MONGO_SERVICE_NAME:\n" +
        "    image: mongo:latest\n" +
        "    environment:\n" +
        "      - MONGO_INITDB_ROOT_USERNAME=$MONGO_USER\n" +
        "      - MONGO_INITDB_ROOT_PASSWORD=$MONGO_PASSWORD\n" )

    return tmpFile
}

fun simpleGet(host: String, port: Int, address: String): String =
    URL("http", host, port, address)
        .openConnection()
        .inputStream
        .bufferedReader()
        .use(BufferedReader::readText)

fun getContent(host: String, port: Int, address: String): Map<String,Any> {
    try {
        val connection = URL("http", host, port, address)
                .openConnection() as HttpURLConnection
        connection.requestMethod = "GET"
        connection.connect()

        return if (HttpStatus.resolve(connection.responseCode)?.is2xxSuccessful == true) {
            val responseBody = connection.getInputStream().bufferedReader().use(BufferedReader::readText)
            mapOf<String, Any>(
                "body" to jacksonObjectMapper().readValue(responseBody),
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode.toString())
        } else mapOf("status" to connection.responseCode.toString())

    } catch (e: Exception){
        return mapOf("error" to e.toString())
    }
}


fun simplePost(host: String, port: Int, address: String,body: String? = null, token: String? = null): Map<String, String> {
    val connection = URL("http", host, port, address).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-type", "application/json")
    connection.setRequestProperty("Accept", "application/json")
    if(token!= null) connection.setRequestProperty("Authorization", "Bearer {$token}")

    try {

    if (body!=null) {
        connection.doOutput = true

        connection.outputStream.bufferedWriter().write(body)
        connection.outputStream.flush()
        connection.outputStream.close()
    }
        val response = mapOf<String,String>(
                "body" to connection.getInputStream().bufferedReader().use (BufferedReader :: readText),
                "header" to connection.getHeaderFields().toString(),
                "status" to getStatus(connection.getHeaderField(0))
        )

        return response

    } catch (e: Exception){
        return mapOf("status" to getStatus(e.message?:"uknown"))
    }
}

fun postWithWritePermission(host: String, port: Int, address: String,body: String? = null, token: String? = null): Map<String, String> {

     val writeToken = "eyJhbGciOiJSUzI1NiIsInR5cCIgOiAiSldUIiwia2lkIiA6ICJYYkJCSlU5dnlDbVZEeFBqV1pmY0V5NzBaSVFrWks2eFYybHhuT09wcVVrIn0.eyJqdGkiOiJjODFkYzExNi02Yzg1LTQ0MGMtOTZkYi0yNjU1Nzg1NzU0ZjciLCJleHAiOjE1NzE3MzA5MzgsIm5iZiI6MCwiaWF0IjoxNTcxNzMwNjM4LCJpc3MiOiJodHRwczovL3Nzby51dDEuZmVsbGVzZGF0YWthdGFsb2cuYnJyZWcubm8vYXV0aC9yZWFsbXMvZmRrIiwiYXVkIjpbImEtYmFja2VuZC1zZXJ2aWNlIiwiZmRrLWhhcnZlc3QtYWRtaW4iLCJvcmdhbml6YXRpb24tY2F0YWxvZ3VlIiwiYWNjb3VudCJdLCJzdWIiOiJmOmFiMTI2ZmU5LTlmZmEtNDE2OS1iZDM5LTFmYzgwMjFiZmQwODoyMzA3NjEwMjI1MiIsInR5cCI6IkJlYXJlciIsImF6cCI6ImZkay1hZG1pbi1ndWkiLCJhdXRoX3RpbWUiOjE1NzE3MzA2MzcsInNlc3Npb25fc3RhdGUiOiJhNDNhZDIzOC1iNzI5LTQyOGItYjNhYi03MGViNWIwZDM3OGEiLCJhY3IiOiIwIiwiYWxsb3dlZC1vcmlnaW5zIjpbImh0dHBzOi8vYWRtaW4udXQxLmZlbGxlc2RhdGFrYXRhbG9nLmJycmVnLm5vIiwiaHR0cDovL2xvY2FsaG9zdDo4MTM3Il0sInJlYWxtX2FjY2VzcyI6eyJyb2xlcyI6WyJvZmZsaW5lX2FjY2VzcyIsInVtYV9hdXRob3JpemF0aW9uIl19LCJyZXNvdXJjZV9hY2Nlc3MiOnsiYWNjb3VudCI6eyJyb2xlcyI6WyJtYW5hZ2UtYWNjb3VudCIsIm1hbmFnZS1hY2NvdW50LWxpbmtzIiwidmlldy1wcm9maWxlIl19fSwic2NvcGUiOiJvcGVuaWQgcHJvZmlsZSBlbWFpbCIsImVtYWlsX3ZlcmlmaWVkIjpmYWxzZSwidXNlcl9uYW1lIjoiMjMwNzYxMDIyNTIiLCJuYW1lIjoiTUFVRCBHVUxMSUtTRU4iLCJwcmVmZXJyZWRfdXNlcm5hbWUiOiIyMzA3NjEwMjI1MiIsImdpdmVuX25hbWUiOiJNQVVEIiwiZmFtaWx5X25hbWUiOiJHVUxMSUtTRU4iLCJhdXRob3JpdGllcyI6InB1Ymxpc2hlcjo5MTAyNDQxMzI6YWRtaW4sc3lzdGVtOnJvb3Q6YWRtaW4ifQ.UbXf00TILi0Qia7xQhuMEi6ONcclmikdyo2wtrXhujL0L2nB8_MC0S0Lyakfm6ZysNRtSw3LPMMHUJypIR85bnmnxVkZrJf0E5d6A81OuXNW7xG9cFHQRM-NmrLAfRwQN7m50fbiG7wuk3oRsIHl2SYc1kfUXBTK3LHJ2EIi9FeCdUtAhKup1scgXNkOciUWp7w80pSI6bZvD9FxnFW5dqaA5F6TPQS-oiQsDQuP1WmtJ5a4_cUYtLNZRa7XId09QONarkNf0VIbybLPdiprvXedM7XK66BX4YffrW7UmX-F-7JJguFh1tfbqsLry5ohSVEV5r1ymAWq0N4Zwd4cGA"
    val connection = URL("http", host, port, address).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-type", "application/json")
    connection.setRequestProperty("Accept", "application/json")
    if(token!= null) connection.setRequestProperty("Authorization", "Bearer {$writeToken}")

    try {

        if (body!=null) {
            connection.doOutput = true

            connection.outputStream.bufferedWriter().write(body)
            connection.outputStream.flush()
            connection.outputStream.close()
        }
        val response = mapOf<String,String>(
                "body" to connection.getInputStream().bufferedReader().use (BufferedReader :: readText),
                "header" to connection.getHeaderFields().toString(),
                "status" to getStatus(connection.getHeaderField(0))
        )

        return response

    } catch (e: Exception){
        return mapOf("status" to getStatus(e.message?:"uknown"))
    }
}


fun getStatus(response: String): String =
    Regex("\\d{3}")
            .find(response)
            ?.value
            ?: "unknown"


