package fr.nicopico.hugo.domain.model

sealed class Screen {
    object Exit: Screen()
    object Loading : Screen()
    object BabySelection : Screen()
    object Timeline : Screen()
}