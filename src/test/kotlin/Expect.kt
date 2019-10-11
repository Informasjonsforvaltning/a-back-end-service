package no.brreg.informasjonsforvaltning.abackendservice

/**
 * Expect assertion style wrapper for jupiter assertions
 */


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions


class Expect (_result: String){
    val result = _result

    fun to_equal(expected: String) {
        Assertions.assertEquals(expected,result)
    }

    fun to_contain(expected: String) {
        Assertions.assertTrue(result.contains(expected))
    }

    fun assume_success(expected: String){
        Assumptions.assumeTrue(result.equals(expected))
    }

}