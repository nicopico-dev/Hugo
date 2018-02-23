package fr.nicopico.hugo.model

data class AppState(
        val user: User?,
        val message: Message? = null,
        val screen: Screen,
        val babies: List<Baby>,
        val selectedBaby: Baby?,
        val timeline: Timeline
)