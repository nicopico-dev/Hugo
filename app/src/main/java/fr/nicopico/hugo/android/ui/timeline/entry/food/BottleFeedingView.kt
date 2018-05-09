package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.constraint.ConstraintLayout
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.textI
import fr.nicopico.hugo.android.utils.textS
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.FoodCare
import kotlinx.android.synthetic.main.view_bottle_feeding.view.*

class BottleFeedingView(
        context: Context,
        content: String? = null
) : ConstraintLayout(context), FoodView {

    constructor(context: Context): this(context, null)

    private lateinit var _content: String

    init {
        inflate(context, R.layout.view_bottle_feeding, this)
        if (content != null) {
            updateBottleContent(content)
        }
    }

    override fun bindTo(foodCare: FoodCare) {
        if (foodCare !is BottleFeeding) throw IllegalArgumentException("foodCare is not BottleFeeding")
        edtBottle.textS = foodCare.volume.toString()
        updateBottleContent(foodCare.content)
    }

    override fun retrieve(): BottleFeeding {
        val volume = edtBottle.asInt()
        return BottleFeeding.create(volume = volume, content = _content)
    }

    private fun updateBottleContent(content: String) {
        _content = content
        txtBottleContent.textI = when (content) {
            BottleFeeding.CONTENT_MATERNAL_MILK -> R.string.maternal_milk
            BottleFeeding.CONTENT_ARTIFICIAL_MILK -> R.string.artificial_milk
            else -> TODO("not implemented")
        }
    }
}