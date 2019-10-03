package no.template

/**
 * Expect assertion style wrapper for jupiter assertions
 */


import org.junit.jupiter.api.Assertions


class Expect (_result: String){
    val result = _result

    fun to_equal(expected: String) {
        Assertions.assertTrue(result.equals(expected))
    }

    fun to_contain(expected: String) {
        Assertions.assertTrue(result.contains(expected))
    }

}