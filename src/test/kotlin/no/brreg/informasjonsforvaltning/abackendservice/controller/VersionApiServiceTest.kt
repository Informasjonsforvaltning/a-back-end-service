package no.brreg.informasjonsforvaltning.abackendservice.controller

import com.nhaarman.mockitokotlin2.*
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus

import javax.servlet.http.HttpServletRequest

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse

@Tag("unit")
class VersionApiServiceTest {

    private val httpServletRequestMock: HttpServletRequest = mock()
    private val versionApi: VersionApiImpl = VersionApiImpl()

    @Test
    fun getVersion() {
        whenever(httpServletRequestMock.getHeader("Accept")).thenReturn("application/json")

        val response = versionApi.getVersion(httpServletRequestMock)

        assertEquals(HttpStatus.OK, response.statusCode)

        assertFalse(response.body?.repositoryUrl.toString().isNullOrBlank())
        assertFalse(response.body?.branchName.isNullOrBlank())
        assertFalse(response.body?.buildTime.isNullOrBlank())
        assertFalse(response.body?.sha.isNullOrBlank())
        assertFalse(response.body?.versionId.isNullOrBlank())
    }
}
