package fr.nicopico.hugo.android.ui.timeline.entry

import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.PUSH_SCREEN

fun ReduxDispatcher.addFoodDialog()
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryAddition(CareType.FOOD)))

fun ReduxDispatcher.editFoodDialog(entry: Timeline.Entry): Unit
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryEdition(entry)))

fun ReduxDispatcher.addChangeDialog()
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryAddition(CareType.CHANGE)))

fun ReduxDispatcher.editChangeDialog(entry: Timeline.Entry)
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryEdition(entry)))

fun ReduxDispatcher.addHealthAndHygieneDialog()
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryAddition(CareType.HEALTH_HYGIENE)))

fun ReduxDispatcher.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryEdition(entry)))