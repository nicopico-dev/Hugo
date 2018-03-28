package fr.nicopico.hugo.domain.model

sealed class User(val uid: String)

class AnonymousUser(uid: String) : User(uid) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AnonymousUser
        if (uid != other.uid) return false

        return true
    }

    override fun hashCode(): Int {
        return uid.hashCode()
    }
}

class RealUser(uid: String, val displayName: String?) : User(uid) {
    override fun toString(): String {
        return "RealUser(displayName=$displayName)"
    }
}