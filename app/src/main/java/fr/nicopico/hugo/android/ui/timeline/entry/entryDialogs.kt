package fr.nicopico.hugo.android.ui.timeline.entry

import android.support.v4.app.Fragment
import fr.nicopico.hugo.android.ui.timeline.entry.change.AddChangeDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.change.EditChangeDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.food.AddFoodDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.food.EditFoodDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.health.AddHealthAndHygieneDialogFragment
import fr.nicopico.hugo.android.ui.timeline.entry.health.EditHealthAndHygieneDialogFragment
import fr.nicopico.hugo.domain.model.Timeline

fun Fragment.addFoodDialog() = AddFoodDialogFragment.create()
        .show(fragmentManager!!, null)

fun Fragment.editFoodDialog(entry: Timeline.Entry): Unit = EditFoodDialogFragment.create(entry)
        .show(fragmentManager!!, null)

fun Fragment.addChangeDialog() = AddChangeDialogFragment.create()
        .show(fragmentManager!!, null)

fun Fragment.editChangeDialog(entry: Timeline.Entry) = EditChangeDialogFragment.create(entry)
        .show(fragmentManager!!, null)

fun Fragment.addHealthAndHygieneDialog() = AddHealthAndHygieneDialogFragment.create()
        .show(fragmentManager!!, null)

fun Fragment.editHealthAndHygieneDialog(entry: Timeline.Entry): Unit = EditHealthAndHygieneDialogFragment.create(entry)
        .show(fragmentManager!!, null)