package fr.nicopico.hugo.android.ui.timeline.entry

import android.support.v4.app.Fragment
import fr.nicopico.hugo.android.ReduxDispatcher
import fr.nicopico.hugo.android.ui.timeline.entry.food.AddFoodDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.food.EditFoodDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.health.AddHealthAndHygieneDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.health.EditHealthAndHygieneDialogFragment
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

fun Fragment.addHealthAndHygieneDialog() = AddHealthAndHygieneDialogFragment.create()
        .show(fragmentManager!!, null)

fun Fragment.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit = EditHealthAndHygieneDialogFragment.create(entry)
        .show(fragmentManager!!, null)