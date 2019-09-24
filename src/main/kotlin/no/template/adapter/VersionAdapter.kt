package no.template.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.template.generated.model.Version
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.net.URI

@Service
class VersionAdapter {

    fun getVersionData(uri: URI): Version {
        val jsonBody = uri.toURL().openConnection().inputStream.bufferedReader().use(BufferedReader::readText)
        return jacksonObjectMapper().readValue(jsonBody)
    }

}