package fr.nicopico.hugo.domain.model

data class AppState(
        val user: User?,
        val message: Message? = null,
        val screen: Screen,
        val babies: List<Baby>,
        val selectedBaby: Baby?,
        val timeline: Timeline,
        val loading: Boolean = false
)