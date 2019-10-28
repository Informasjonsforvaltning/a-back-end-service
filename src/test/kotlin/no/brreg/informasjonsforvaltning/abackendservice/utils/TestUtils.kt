package no.brreg.informasjonsforvaltning.abackendservice.utils

import java.io.BufferedReader
import java.io.File
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.OutputStreamWriter

fun apiGet(endpoint: String): Map<String,Any> {
    try {
    val connection = URL(getApiAddress(endpoint))
            .openConnection() as HttpURLConnection

    val responseBody = connection.getInputStream().bufferedReader().use (BufferedReader :: readText)
    val response = mapOf<String,Any>(
            "body" to jacksonObjectMapper().readValue(responseBody),
            "header" to connection.getHeaderFields().toString(),
            "status" to connection.responseCode)

        return response

    } catch (e: Exception){
        return mapOf("error" to e.toString())
    }
}


fun apiPost(endpoint : String, body: String?, token: String?): Map<String, String> {
    val connection  = URL(getApiAddress(endpoint)).openConnection() as HttpURLConnection
    connection.requestMethod = "POST"
    connection.setRequestProperty("Content-type", "application/json")
    connection.setRequestProperty("Accept", "application/json")

    if(!token.isNullOrEmpty()) {connection.setRequestProperty("Authorization", "Bearer $token")}

    try {
        if (body!=null) {
            connection.doOutput = true
            val writer = OutputStreamWriter(connection.outputStream)
            writer.write(body)
            writer.close()

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


