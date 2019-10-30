package no.brreg.informasjonsforvaltning.abackendservice.utils

import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.springframework.http.HttpStatus
import java.io.OutputStreamWriter

fun apiGet(endpoint: String): Map<String,Any> {

    return try{
        val connection = URL(getApiAddress(endpoint))
                .openConnection() as HttpURLConnection
        val responseBody = connection.getInputStream().bufferedReader().use (BufferedReader :: readText)
        mapOf(
                "body" to jacksonObjectMapper().readValue(responseBody),
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode)

    } catch (e: Exception){
        mapOf("status" to getStatus(e.message?:"uknown"))
    }
}


fun apiPost(endpoint : String, body: String?, token: String?): Map<String, String> {
    val connection  = URL(getApiAddress(endpoint)).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    if(!token.isNullOrEmpty()) {connection.setRequestProperty("Authorization", "Bearer $token")}

    return try {
        connection.doOutput = true
        connection.connect();

        if(body != null) {
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(body)
            writer.close()
        }

        if(isOK(connection.responseCode)){
            mapOf(
                "body" to connection.inputStream.bufferedReader().use(BufferedReader :: readText),
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode.toString()
            )
        } else {
            mapOf<String,String>(
                    "status" to connection.responseCode.toString()
            )
        }
    } catch (e: Exception) {
        mapOf("status" to getStatus(e.message?:"unknown"))
    }
}

fun getStatus(response: String): String =
    Regex("\\d{3}")
            .find(response)
            ?.value
            ?:response

fun isOK(response: Int?): Boolean =
        if(response == null) false
        else HttpStatus.resolve(response)?.is2xxSuccessful == true


