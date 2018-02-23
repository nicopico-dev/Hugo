package fr.nicopico.hugo.model

sealed class Screen {
    object Exit: Screen()
    object Login : Screen()
    object BabySelection : Screen()
    object Timeline : Screen()
}