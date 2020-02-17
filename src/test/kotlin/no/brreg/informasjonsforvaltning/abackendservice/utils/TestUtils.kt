package no.brreg.informasjonsforvaltning.abackendservice.utils

import java.io.BufferedReader
import java.net.HttpURLConnection
import java.net.URL
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.mongodb.MongoClient
import org.springframework.http.HttpStatus
import java.io.OutputStreamWriter
import com.mongodb.MongoClientURI
import no.brreg.informasjonsforvaltning.abackendservice.utils.ApiTestContainer.Companion.mongoContainer
import org.bson.codecs.configuration.CodecRegistries.fromProviders
import org.bson.codecs.configuration.CodecRegistries.fromRegistries
import org.bson.codecs.pojo.PojoCodecProvider


fun apiGet(endpoint: String): Map<String,Any> {

    return try{
        val connection = URL(getApiAddress(endpoint))
                .openConnection() as HttpURLConnection

        connection.connect()
        if(isOK(connection.responseCode)) {
            val responseBody = connection.getInputStream().bufferedReader().use(BufferedReader::readText)
            mapOf(
                    "body"   to jacksonObjectMapper().readValue(responseBody),
                    "header" to connection.headerFields.toString(),
                    "status" to connection.responseCode)
        } else {
            mapOf(
                    "status" to connection.responseCode,
                    "header" to " ",
                    "body"   to " "
            )
        }
    } catch (e: Exception){
         mapOf(
                 "status" to e.toString(),
                 "header" to " ",
                 "body"   to " "
        )
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
                "body"   to connection.inputStream.bufferedReader().use(BufferedReader :: readText),
                "header" to connection.headerFields.toString(),
                "status" to connection.responseCode.toString()
            )
        } else {
            mapOf<String,String>(
                    "status" to connection.responseCode.toString(),
                    "header" to " ",
                    "body" to " "
            )
        }
    } catch (e: Exception) {
        mapOf(
                "status" to getStatus(e.message?:"unknown"),
                "header" to " ",
                "body"   to " "
        )
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

fun populateDB(){
    val uri= "mongodb://${MONGO_USER}:${MONGO_PASSWORD}@localhost:${mongoContainer.getMappedPort(MONGO_PORT)}/a-backend-service?authSource=admin&authMechanism=SCRAM-SHA-1"
    val pojoCodecRegistry = fromRegistries(MongoClient.getDefaultCodecRegistry(), fromProviders(PojoCodecProvider.builder().automatic(true).build()))

    val mongoClient = MongoClient(
            MongoClientURI(uri)
    )
    val mongoDatabase = mongoClient.getDatabase("a-backend-service").withCodecRegistry(pojoCodecRegistry)
    val mongoCollection = mongoDatabase.getCollection("service-endpoints")
    mongoCollection.insertOne(dataForPopulate())

    val success = mongoCollection.find();
    mongoClient.close()
}

