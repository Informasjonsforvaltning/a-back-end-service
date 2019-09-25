package no.template.adapter

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import no.template.generated.model.Version
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.net.URL

@Service
class VersionAdapter {

    fun getVersionData(url: URL): Version {
        val jsonBody = url
            .openConnection()
            .inputStream
            .bufferedReader()
            .use(BufferedReader::readText)

        return jacksonObjectMapper().readValue(jsonBody)
    }

}