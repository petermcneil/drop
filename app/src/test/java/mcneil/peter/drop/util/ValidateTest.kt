package mcneil.peter.drop.util

import mcneil.peter.drop.model.Either
import org.junit.Assert.fail
import org.junit.Test

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ValidateTest {

    @Test
    fun emailValid() {
        val ex = Validate.email("harry@example.com")
        if (ex is Either.Right) {
            assert(ex.value)
        } else {
            fail()
        }

        val pop = Validate.email("harry@pop.ski")
        if (pop is Either.Right) {
            assert(pop.value)
        } else {
            fail()
        }
    }

    @Test
    fun emailInvalid() {
        val t1 = Validate.email("harry@")
        val t2 = Validate.email("harry@com")
        val t3 = Validate.email("harry@example.")

        if (t1 is Either.Right) {
            fail()
        }
        if (t2 is Either.Right) {
            fail()
        }
        if (t3 is Either.Right) {
            fail()
        }

    }

    @Test
    fun passwordValid() {
        val p1 = Validate.password("12345678")
        if(p1 is Either.Right) {
            assert(p1.value)
        }
    }

    @Test
    fun passwordInvalid() {
        val p1 = Validate.password("1234567")
        if (p1 is Either.Right) {
            fail()
        } }
}
