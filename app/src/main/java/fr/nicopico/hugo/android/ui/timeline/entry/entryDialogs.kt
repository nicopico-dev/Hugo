package fr.nicopico.hugo.android.ui.timeline.entry

import android.support.v4.app.Fragment
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.ui.timeline.entry.food.AddFoodDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.food.EditFoodDialogFragment
import fr.nicopico.hugo.domain.model.CareType
import fr.nicopico.hugo.domain.model.Screen
import fr.nicopico.hugo.domain.model.Timeline
import fr.nicopico.hugo.domain.redux.PUSH_SCREEN

fun Fragment.addFoodDialog() = AddFoodDialogFragment.create()
        .show(fragmentManager!!, null)

fun Fragment.editFoodDialog(entry: Timeline.Entry): Unit = EditFoodDialogFragment.create(entry)
        .show(fragmentManager!!, null)

fun ReduxDispatcher.addChangeDialog()
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryAddition(CareType.CHANGE)))

fun ReduxDispatcher.editChangeDialog(entry: Timeline.Entry)
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryEdition(entry)))

fun ReduxDispatcher.addHealthAndHygieneDialog()
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryAddition(CareType.HEALTH_HYGIENE)))

fun ReduxDispatcher.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit
        = dispatch(PUSH_SCREEN(Screen.TimelineEntryEdition(entry)))