package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.Breast
import fr.nicopico.hugo.domain.model.BreastExtraction
import kotlinx.android.synthetic.main.view_breast_extraction.view.*
import java.util.*

class BreastExtractionView(context: Context) : ConstraintLayout(context), FoodView<BreastExtraction> {

    init {
        inflate(context, R.layout.view_breast_extraction, this)
    }

    fun bindTo(care: BreastExtraction): BreastExtractionView {
        edtExtraction.textS = care.volume.toString()
        chkLeftBreast.isChecked = Breast.LEFT in care.breasts
        chkRightBreast.isChecked = Breast.RIGHT in care.breasts
        return this
    }

    override fun retrieve(): BreastExtraction {
        val volume = edtExtraction.asInt()
        val leftBreast = chkLeftBreast.isChecked
        val rightBreast = chkRightBreast.isChecked
        val breasts = when {
            leftBreast && rightBreast -> EnumSet.allOf(Breast::class.java)
            leftBreast -> EnumSet.of(Breast.LEFT)
            rightBreast -> EnumSet.of(Breast.RIGHT)
            else -> EnumSet.noneOf(Breast::class.java)
        }
        return BreastExtraction(volume = volume, breasts = breasts)
    }
}