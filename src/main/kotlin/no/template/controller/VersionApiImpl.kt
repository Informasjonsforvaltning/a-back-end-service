package no.template.controller

import no.template.generated.model.Version
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.*


@Controller
open class VersionApiImpl : no.template.generated.api.VersionApi {

    private var properties : Properties? = null


    override fun getVersion(httpServletRequest: HttpServletRequest?): ResponseEntity<Version> {
        if (properties == null) {
            properties = Properties()
            properties!!.load(this::class.java.getResourceAsStream("/git.properties"))
        }

        val version = Version()
        version.branchName = properties!!.getProperty("git.branch")
        version.buildTime = properties!!.getProperty("git.build.time")
        version.sha = properties!!.getProperty("git.commit.id")
        version.versionId = properties!!.getProperty("git.build.version")

        return version.let { response -> ResponseEntity(response, HttpStatus.OK) }
    }

}
