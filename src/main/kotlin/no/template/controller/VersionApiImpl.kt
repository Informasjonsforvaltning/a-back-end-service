package no.template.controller

import no.template.generated.model.Version
import javax.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import java.util.*

private val properties : Properties = Properties()

@Controller
open class VersionApiImpl : no.template.generated.api.VersionApi {

    override fun getVersion(httpServletRequest: HttpServletRequest): ResponseEntity<Version> {
        if (properties.isEmpty) {
            properties.load(this::class.java.getResourceAsStream("/git.properties"))
        }

        return Version()
            .apply {
                repositoryName = getRepositoryName(properties.getProperty("git.remote.origin.url"))
                branchName = properties.getProperty("git.branch")
                buildTime = properties.getProperty("git.build.time")
                sha = properties.getProperty("git.commit.id")
                versionId = properties.getProperty("git.build.version") }
            .let { response -> ResponseEntity(response, HttpStatus.OK) }
    }

}

private fun getRepositoryName(remoteOriginUrl: String): String? =
    Regex("""(?<=/).*?(?=.git)""")
        .find(remoteOriginUrl)?.value
