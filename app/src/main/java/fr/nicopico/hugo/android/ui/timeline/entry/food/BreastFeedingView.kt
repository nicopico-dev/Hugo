package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.FoodCare
import kotlinx.android.synthetic.main.view_breast_feeding.view.*

class BreastFeedingView(context: Context) : ConstraintLayout(context), FoodView {

    init {
        inflate(context, R.layout.view_breast_feeding, this)
    }

    override fun bindTo(foodCare: FoodCare) {
        if (foodCare !is BreastFeeding) throw IllegalArgumentException("foodCare is not BreastFeeding")
        edtLeftBreastDuration.textS = foodCare.leftDuration?.toString()
        edtRightBreastDuration.textS = foodCare.rightDuration?.toString()
    }

    override fun retrieve(): BreastFeeding {
        return BreastFeeding(
                leftDuration = edtLeftBreastDuration.asIntOrNull(),
                rightDuration = edtRightBreastDuration.asIntOrNull()
        )
    }
}