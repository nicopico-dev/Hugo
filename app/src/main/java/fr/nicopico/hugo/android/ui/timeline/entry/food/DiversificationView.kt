package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context

import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.Diversification
import kotlinx.android.synthetic.main.view_diversification.view.*

class DiversificationView(context: Context) : ConstraintLayout(context), FoodView<Diversification> {

    init {
        inflate(context, R.layout.view_diversification, this)
    }

    fun bindTo(care: Diversification): DiversificationView {
        edtAlimentName.textS = care.aliment
        edtAlimentQuantity.textS = care.quantity.toString()
        return this
    }

    override fun retrieve(): Diversification {
        return Diversification(
                quantity = edtAlimentQuantity.asInt(),
                aliment = edtAlimentName.text.toString()
        )
    }
}