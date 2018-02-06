package fr.nicopico.hugo.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.nicopico.hugo.model.AnonymousUser
import fr.nicopico.hugo.model.User
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

interface AuthService {
    companion object {
        fun create(): AuthService = FirebaseAuthService()
    }

    suspend fun signIn(): Result<User>
}

private class FirebaseAuthService : AuthService {

    private val auth by lazy { FirebaseAuth.getInstance() }

    private val currentUser: User?
        get() = auth.currentUser?.convert()

    override suspend fun signIn(): Result<User> = suspendCancellableCoroutine { cont ->
        val currentUser = currentUser
        if (currentUser != null) {
            cont.resume(Result.Success(currentUser))
            return@suspendCancellableCoroutine
        }

        auth.signInAnonymously().addOnCompleteListener {
            if (!cont.isCancelled) {
                if (it.isSuccessful) {
                    it.result.user.convert().let { user ->
                        cont.resume(Result.Success(user))
                    }
                } else {
                    cont.resume(Result.Failure(it.exception ?: Exception("Unknown error")))
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