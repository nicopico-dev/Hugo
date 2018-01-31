package fr.nicopico.hugo.service

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import fr.nicopico.hugo.utils.Result
import kotlinx.coroutines.experimental.suspendCancellableCoroutine

typealias OnUserChangeListener = (User) -> Unit

interface AuthService {
    companion object {
        val INSTANCE: AuthService by lazy { FirebaseAuthService }
    }

    fun addOnUserChangeListener(listener: OnUserChangeListener)
    fun removeOnUserChangeListener(listener: OnUserChangeListener)
    suspend fun signIn(): Result<User>
}

private object FirebaseAuthService : AuthService {

    private val auth = FirebaseAuth.getInstance()

    private val currentUser: User? = auth.currentUser?.convert()

    private val onUserChangeListeners = mutableListOf<OnUserChangeListener>()
    override fun addOnUserChangeListener(listener: OnUserChangeListener) {
        onUserChangeListeners += listener
        currentUser?.let { listener(it) }
    }
    override fun removeOnUserChangeListener(listener: OnUserChangeListener) {
        onUserChangeListeners -= listener
    }

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
                        notifyUserChange(user)
                    }
                } else {
                    cont.resume(Result.Failure(it.exception ?: Exception("Unknown error")))
                }
            }
        }
    }

    private fun notifyUserChange(user: User) {
        for (listener in onUserChangeListeners) {
            listener(user)
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