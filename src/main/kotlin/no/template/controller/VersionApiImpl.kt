package no.template.controller

import no.template.generated.model.Version
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.*


@Controller
open class VersionApiImpl : no.template.generated.api.VersionApi {

    private var properties : Properties = Properties()


    override fun getVersion(httpServletRequest: HttpServletRequest?): ResponseEntity<Version> {
        if (properties.isEmpty) {
            properties.load(this::class.java.getResourceAsStream("/git.properties"))
        }

        return Version().apply {
            branchName = properties.getProperty("git.branch")
            buildTime = properties.getProperty("git.build.time")
            sha = properties.getProperty("git.commit.id")
            versionId = properties.getProperty("git.build.version")
        }.let { response -> ResponseEntity(response, HttpStatus.OK) }
    }

}
