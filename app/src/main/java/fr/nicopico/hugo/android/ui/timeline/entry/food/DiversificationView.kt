package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.FoodCare
import kotlinx.android.synthetic.main.view_diversification.view.*

class DiversificationView(context: Context) : ConstraintLayout(context), FoodView {

    init {
        inflate(context, R.layout.view_diversification, this)
    }

    override fun bindTo(foodCare: FoodCare) {
        if (foodCare !is Diversification) throw IllegalArgumentException("foodCare is not Diversification")
        edtAlimentName.textS = foodCare.aliment
        edtAlimentQuantity.textS = foodCare.quantity.toString()
    }

    override fun retrieve(): Diversification {
        return Diversification(
                quantity = edtAlimentQuantity.asInt(),
                aliment = edtAlimentName.text.toString()
        )
    }
}