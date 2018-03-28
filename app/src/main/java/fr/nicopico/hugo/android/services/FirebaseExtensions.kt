package fr.nicopico.hugo.android.services

import com.google.firebase.auth.FirebaseUser
import fr.nicopico.hugo.domain.model.AnonymousUser
import fr.nicopico.hugo.domain.model.RealUser
import fr.nicopico.hugo.domain.model.User


fun FirebaseUser.convert(): User {
    return if (isAnonymous) {
        AnonymousUser(uid)
    } else {
        RealUser(uid, displayName)
    }
}