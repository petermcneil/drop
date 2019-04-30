package mcneil.peter.drop.model

sealed class Either<out A, out B> {
    class Left<out A>(val value: A) : Either<A, Nothing>()
    class Right<out B>(val value: B) : Either<Nothing, B>()

    fun <A> right(value: A): Either<Nothing, A> = Right(value)
    fun <B> left(value: B): Either<B, Nothing> = Left(value)
}

interface ACallback<T> {
    fun callback(ret: T)
}