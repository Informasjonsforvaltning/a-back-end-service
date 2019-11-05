package no.brreg.informasjonsforvaltning.abackendservice.utils

/**
 * Expect assertion style wrapper for jupiter assertions
 */


import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assumptions


class Expect(_result: Any?){

    val result = _result

    fun to_equal(expected: String) {
        Assertions.assertEquals(expected,result)
    }
    fun to_equal(expected: Int) {
        Assertions.assertEquals(expected,result)
    }

    fun to_contain(expected: String) {
        when(result) {
           is String -> Assertions.assertTrue(result.contains(expected), "expected string to contain $expected")
           is LinkedHashMap<*, *> -> Assertions.assertTrue(result.contains(expected))
           else -> throw AssertionError("Unexpected datatype in result");
        }
    }
}

fun assume_authenticated(status: String) {
    Assumptions.assumeFalse(status.equals("401"))
}

fun assume_success(status: String) {
    Assumptions.assumeTrue(status.equals("201"))
}

fun assume_implemented(status: String){
    Assumptions.assumeFalse(status.equals("501"))
}