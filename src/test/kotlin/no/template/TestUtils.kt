package no.template

import java.io.BufferedReader
import java.io.File
import java.io.IOException
import java.net.HttpURLConnection
import java.net.URL

fun createTmpComposeFile(): File {
    val tmpFile = File.createTempFile("test-compose", ".yml")

    tmpFile.writeText("version: \"3.2\"\n" +
        "\n" +
        "services:\n" +
        "  $API_SERVICE_NAME:\n" +
        "    image: brreg/template-image-name:latest\n" +
        "    environment:\n" +
        "      - TEMPLATE_MONGO_USERNAME=$MONGO_USER\n" +
        "      - TEMPLATE_MONGO_PASSWORD=$MONGO_PASSWORD\n" +
        "      - TEMPLATE_MONGO_HOST=$MONGO_SERVICE_NAME:$MONGO_PORT\n" +
        "    depends_on:\n" +
        "      - $MONGO_SERVICE_NAME\n" +
        "\n" +
        "  $MONGO_SERVICE_NAME:\n" +
        "    image: mongo:latest\n" +
        "    environment:\n" +
        "      - MONGO_INITDB_ROOT_USERNAME=$MONGO_USER\n" +
        "      - MONGO_INITDB_ROOT_PASSWORD=$MONGO_PASSWORD\n")

    return tmpFile
}

fun simpleGet(host: String, port: Int, address: String): String =
    URL("http", host, port, address)
        .openConnection()
        .inputStream
        .bufferedReader()
        .use(BufferedReader::readText)

fun getContent(host: String, port: Int, address: String): Map<String,String> {
    val connection = URL("http", host, port, address)
            .openConnection()
    val response = mapOf<String,String>(
            "body" to connection.getInputStream().bufferedReader().use (BufferedReader :: readText),
            "header" to connection.getHeaderFields().toString(),
            "status" to connection.getHeaderField(0).split(" ")[1]
    )
    return response
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
                "status" to connection.getHeaderField(0).split(" ")[1]
        )

        return response

    } catch (e: Exception){
        val status = e.message?:"unknown"
        return mapOf("status" to status)
    }
}