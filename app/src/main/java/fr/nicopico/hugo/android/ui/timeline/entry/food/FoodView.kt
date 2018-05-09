package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.widget.TextView
import fr.nicopico.hugo.domain.model.FoodCare

interface FoodView {
    fun bindTo(foodCare: FoodCare)
    fun retrieve(): FoodCare

    fun TextView.asInt(): Int = asIntOrNull()!!
    fun TextView.asIntOrNull(): Int? = when {
        text.isNullOrEmpty() -> null
        else -> Integer.parseInt(text.toString())
    }
}

