package fr.nicopico.hugo.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.suspendCancellableCoroutine


object AuthHelper {

    private val auth = FirebaseAuth.getInstance()

    val currentUser: User?
        get() = auth.currentUser?.convert()

    suspend fun signIn(): Result<User> = suspendCancellableCoroutine { cont ->
        val currentUser = currentUser
        if (currentUser != null) {
            cont.resume(Result.Success(currentUser))
            return@suspendCancellableCoroutine
        }

        auth.signInAnonymously().addOnCompleteListener {
            if (!cont.isCancelled) {
                if (it.isSuccessful) {
                    cont.resume(Result.Success(it.result.user.convert()))
                } else {
                    cont.resume(Result.Failure(it.exception))
                }
            }
        }
    }
}

private fun FirebaseUser.convert(): User {
    if (isAnonymous) {
        return AnonymousUser(uid)
    } else {
        TODO("No support for authenticated user yet")
    }
}

sealed class User
data class AnonymousUser(val uid: String) : User()