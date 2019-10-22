package no.brreg.informasjonsforvaltning.abackendservice.utils

import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue


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
            .openConnection()
    val responseBody = connection.getInputStream().bufferedReader().use (BufferedReader :: readText)
    val response = mapOf<String,Any>(
            "body" to jacksonObjectMapper().readValue(responseBody),
            "header" to connection.getHeaderFields().toString(),
            "status" to getStatus(connection.getHeaderField(0)))

        return response

    } catch (e: Exception){
        return mapOf("status" to getStatus(e.message?:"uknown"))
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

fun getStatus(response: String): String =
    Regex("\\d{3}")
            .find(response)
            ?.value
            ?: "unkown"


