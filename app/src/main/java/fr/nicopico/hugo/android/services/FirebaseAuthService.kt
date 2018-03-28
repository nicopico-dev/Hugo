package fr.nicopico.hugo.android.services

import com.google.firebase.auth.FirebaseAuth
import fr.nicopico.hugo.domain.model.User
import fr.nicopico.hugo.domain.services.AuthService

class FirebaseAuthService : AuthService {

    private val auth by lazy { FirebaseAuth.getInstance() }

    override val currentUser: User?
        get() = auth.currentUser?.convert()
}