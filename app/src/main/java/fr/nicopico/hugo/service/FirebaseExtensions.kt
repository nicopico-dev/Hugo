package fr.nicopico.hugo.service

import com.google.firebase.auth.FirebaseUser
import fr.nicopico.hugo.model.AnonymousUser
import fr.nicopico.hugo.model.RealUser
import fr.nicopico.hugo.model.User


fun FirebaseUser.convert(): User {
    return if (isAnonymous) {
        AnonymousUser(uid)
    } else {
        RealUser(uid, displayName)
    }
}