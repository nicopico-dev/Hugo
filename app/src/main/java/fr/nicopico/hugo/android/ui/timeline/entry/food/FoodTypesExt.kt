package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import fr.nicopico.hugo.R
import fr.nicopico.hugo.domain.model.BottleFeeding
import fr.nicopico.hugo.domain.model.BreastExtraction
import fr.nicopico.hugo.domain.model.BreastFeeding
import fr.nicopico.hugo.domain.model.Diversification
import fr.nicopico.hugo.domain.model.FoodType

fun FoodType.getLabelResId(): Int = when(this) {
    BreastFeeding::class -> R.string.care_food_breast_feeding
    BreastExtraction::class -> R.string.care_food_extraction
    BottleFeeding.Maternal::class -> R.string.care_food_maternal_bottle_feeding
    BottleFeeding.Artificial::class -> R.string.care_food_artificial_bottle_feeding
    Diversification::class -> R.string.care_food_diversification
    else -> throw UnsupportedOperationException("Unknown FoodType $this")
}

fun FoodType.getLabel(context: Context): String = context.getString(this.getLabelResId())