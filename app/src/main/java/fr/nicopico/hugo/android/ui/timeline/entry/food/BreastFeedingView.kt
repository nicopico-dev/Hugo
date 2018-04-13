package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.BreastFeeding
import kotlinx.android.synthetic.main.view_breast_feeding.view.*

class BreastFeedingView(context: Context) : ConstraintLayout(context), FoodView<BreastFeeding> {

    init {
        inflate(context, R.layout.view_breast_feeding, this)
    }

    fun bindTo(care: BreastFeeding): BreastFeedingView {
        edtLeftBreastDuration.textS = care.leftDuration?.toString()
        edtRightBreastDuration.textS = care.rightDuration?.toString()
        return this
    }

    override fun retrieve(): BreastFeeding {
        return BreastFeeding(
                leftDuration = edtLeftBreastDuration.asIntOrNull(),
                rightDuration = edtRightBreastDuration.asIntOrNull()
        )
    }
}