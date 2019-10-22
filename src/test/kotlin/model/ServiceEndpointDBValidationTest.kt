package model

import no.brreg.informasjonsforvaltning.abackendservice.utils.TestNames
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import no.brreg.informasjonsforvaltning.abackendservice.utils.TestUrls
import no.brreg.informasjonsforvaltning.abackendservice.utils.createServiceEndpointDB
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import javax.validation.Validation
import javax.validation.Validator


@Tag("Unit")
class ServiceEndpointDBValidationTest {

    private val validator: Validator = Validation
                   .buildDefaultValidatorFactory()
                   .validator

    @Test
    fun `expect ServiceEndpointDb constraint violations to be empty`(){
        val dbEndpoint = createServiceEndpointDB(TestNames.CORRECT, TestUrls.CORRECT)
        val violations = validator.validate(dbEndpoint)

        assertTrue(violations.isEmpty())

    }

    @Test
    fun `expect ServiceEndpointDb constraint violations to fail on name with whitespace`(){
        val dbEndpoint = createServiceEndpointDB(TestNames.WITH_WHITE_SPACE, TestUrls.CORRECT)
        val violations = validator.validate(dbEndpoint)
        val violationPath = violations.last().propertyPath.toString()

        assertFalse(violations.isEmpty())
        assertEquals(violationPath,"name")
    }

}