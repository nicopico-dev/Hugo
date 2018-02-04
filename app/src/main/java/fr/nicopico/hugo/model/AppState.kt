package fr.nicopico.hugo.model

import fr.nicopico.hugo.service.User

data class AppState(
        val user: User?,
        val loading: Boolean = false,
        val timeline: Timeline
)