package fr.nicopico.hugo.domain.redux

import fr.nicopico.hugo.domain.model.Timeline

data class AppState(
        val timeline: List<Timeline.Entry>
)