package fr.nicopico.hugo.domain.services

import fr.nicopico.hugo.domain.model.User

interface UserService {
    val currentUser: User?
}