package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.domain.model.FoodType
import kotlinx.android.extensions.LayoutContainer

class FoodChoiceAdapter(
        context: Context
) : ListAdapter<FoodType, FoodChoiceAdapter.ViewHolder>(FoodCareTypeDiffCallback) {

    var click: (FoodType) -> Unit = {}
    private var removedFoodTypes = emptyList<FoodType>()
    private var submittedList = emptyList<FoodType>()

    fun add(foodType: FoodType) {
        removedFoodTypes -= foodType
        refresh()
    }

    fun remove(foodType: FoodType) {
        removedFoodTypes += foodType
        refresh()
    }

    override fun submitList(list: List<FoodType>) {
        submittedList = list
        refresh()
    }

    private fun refresh() {
        super.submitList(submittedList - removedFoodTypes)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        TODO("onCreateViewHolder is not implemented")
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        TODO("onBindViewHolder is not implemented")
    }

    class ViewHolder(
            override val containerView: View?
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer
}

private object FoodCareTypeDiffCallback : DiffUtil.ItemCallback<FoodType>() {
    override fun areItemsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
        return oldItem == newItem
    }
}