package fr.nicopico.hugo.domain.utils

@Suppress("unused")
sealed class Result<T> {
    class Success<T>(val value: T) : Result<T>()
    class Failure<T>(val error: Exception) : Result<T>()
}