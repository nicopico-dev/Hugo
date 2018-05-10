package fr.nicopico.hugo.android.ui.babies

import android.content.Context
import android.support.v7.recyclerview.extensions.ListAdapter
import android.support.v7.util.DiffUtil
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import fr.nicopico.hugo.R
import fr.nicopico.hugo.android.utils.click
import fr.nicopico.hugo.android.utils.longClick
import fr.nicopico.hugo.domain.model.Baby
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_baby.*

class BabyAdapter(context: Context) : ListAdapter<Baby, BabyAdapter.ViewHolder>(BabyDiffCallback) {

    private val inflater = LayoutInflater.from(context)
    private var itemClickListener: BabyListener? = null
    private var itemLongClickListener: BabyListener? = null

    fun itemClick(listener: BabyListener) {
        this.itemClickListener = listener
    }

    fun itemLongClick(listener: BabyListener) {
        this.itemLongClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            inflater.inflate(R.layout.item_baby, parent, false)
                    .let(::ViewHolder)

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(getItem(position))
    }

    inner class ViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private lateinit var baby: Baby

        init {
            containerView.click { itemClickListener?.invoke(baby) }
            containerView.longClick { itemLongClickListener?.invoke(baby) }
        }

        fun bindTo(baby: Baby) {
            this.baby = baby
            txtBabyName.text = baby.name
        }
    }
}

private typealias BabyListener = (Baby) -> Unit

private val BabyDiffCallback = object : DiffUtil.ItemCallback<Baby>() {
    override fun areItemsTheSame(oldItem: Baby, newItem: Baby): Boolean {
        return oldItem.key == newItem.key
    }

    override fun areContentsTheSame(oldItem: Baby, newItem: Baby): Boolean {
        return oldItem == newItem
    }
}