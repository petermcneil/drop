package mcneil.peter.drop.util

import org.junit.Assert.assertFalse
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ValidateTest {

    @Test
    fun emailValid() {
        assert(Validate.email("harry@example.com").first)
        assert(Validate.email("harry@pop.ski").first)
    }

    @Test
    fun emailInvalid() {
        assertFalse(Validate.email("harry@").first)
        assertFalse(Validate.email("harry@com").first)
        assertFalse(Validate.email("harry@example.").first)
    }

    @Test
    fun passwordValid() {
        assert(Validate.password("12345678").first)
    }

    @Test
    fun passwordInvalid() {
        assertFalse(Validate.password("1234567").first)
    }
}
