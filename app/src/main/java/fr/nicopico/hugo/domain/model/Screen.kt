package fr.nicopico.hugo.domain.model

import fr.nicopico.hugo.domain.model.Timeline as tl

sealed class Screen {
    object Exit: Screen()
    object Loading : Screen()
    object BabySelection : Screen()
    object BabyAddition : Screen()
    class BabyEdition(val baby: Baby) : Screen()
    object Timeline : Screen()
}