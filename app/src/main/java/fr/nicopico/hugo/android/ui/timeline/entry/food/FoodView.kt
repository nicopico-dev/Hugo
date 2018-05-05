package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.widget.TextView
import fr.nicopico.hugo.domain.model.FoodCare

interface FoodView<out T : FoodCare> {
    fun retrieve(): T

    fun TextView.asInt(): Int = asIntOrNull()!!
    fun TextView.asIntOrNull(): Int? = when {
        text.isNullOrEmpty() -> null
        else -> Integer.parseInt(text.toString())
    }
}

