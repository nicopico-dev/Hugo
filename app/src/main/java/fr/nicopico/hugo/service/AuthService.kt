package fr.nicopico.hugo.service

import com.google.firebase.auth.FirebaseAuth
import fr.nicopico.hugo.model.User

interface AuthService {
    companion object {
        fun create(): AuthService = FirebaseAuthService()
    }

    val currentUser: User?
}

private class FirebaseAuthService : AuthService {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override val currentUser: User?
        get() = auth.currentUser?.convert()
}