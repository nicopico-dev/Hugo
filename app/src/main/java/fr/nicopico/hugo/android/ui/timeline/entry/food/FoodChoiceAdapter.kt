package fr.nicopico.hugo.android.ui.timeline.entry.food

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.textI
import fr.nicopico.hugo.domain.model.FoodType
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_food_choice.*

typealias FoodChoiceListener = (FoodType) -> Unit

class FoodChoiceAdapter(
        context: Context
) : ListAdapter<FoodType, FoodChoiceAdapter.ViewHolder>(FoodCareTypeDiffCallback) {

    private var removedFoodTypes = emptyList<FoodType>()
    private var submittedList = emptyList<FoodType>()
    private var itemClickListener: FoodChoiceListener = {}

    private val layoutInflater by lazy { LayoutInflater.from(context) }

    fun itemClick(listener: FoodChoiceListener) {
        itemClickListener = listener
    }

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            layoutInflater.inflate(R.layout.item_food_choice, parent, false)
                    .let { ViewHolder(it) }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    inner class ViewHolder(
            override val containerView: View?
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private lateinit var foodType: FoodType

        init {
            itemView.click { itemClickListener.invoke(foodType) }
        }

        fun bind(foodType: FoodType) {
            this.foodType = foodType
            txtLabel.textI = foodType.getLabelResId()
        }
    }
}

private object FoodCareTypeDiffCallback : DiffUtil.ItemCallback<FoodType>() {
    override fun areItemsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: FoodType, newItem: FoodType): Boolean {
        return oldItem == newItem
    }
}