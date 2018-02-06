package fr.nicopico.hugo.model

data class AppState(
        val user: User?,
        val loading: Boolean = false,
        val message: Message? = null,
        val babies: List<Baby>,
        val selectedBaby: Baby?,
        val timeline: Timeline
)