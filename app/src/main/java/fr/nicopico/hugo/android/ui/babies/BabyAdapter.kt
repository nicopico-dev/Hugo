package fr.nicopico.hugo.android.ui.babies

import android.content.Context
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
import kotlin.properties.Delegates

private typealias BabyListener = (Baby) -> Unit

class BabyAdapter(
        context: Context,
        data: List<Baby> = emptyList()
) : RecyclerView.Adapter<BabyAdapter.ViewHolder>() {

    private val inflater = LayoutInflater.from(context)
    var data: List<Baby> by Delegates.observable(data) { _, old, new ->
        val callback = BabyEntryDiffUtilCallback(old, new)
        val diffResult = DiffUtil.calculateDiff(callback, false)
        diffResult.dispatchUpdatesTo(this)
    }
    var clickListener: BabyListener? = null
    var longClickListener: BabyListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder =
            inflater.inflate(R.layout.item_baby, parent, false)
                    .let(::ViewHolder)

    override fun getItemCount(): Int = data.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindTo(data[position])
    }

    inner class ViewHolder(
            override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private lateinit var baby: Baby

        init {
            containerView.click { clickListener?.invoke(baby) }
            containerView.longClick { longClickListener?.invoke(baby) }
        }

        fun bindTo(baby: Baby) {
            this.baby = baby
            txtBabyName.text = baby.name
        }
    }

    private class BabyEntryDiffUtilCallback(
            val old: List<Baby>,
            val new: List<Baby>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int = old.size

        override fun getNewListSize(): Int = new.size

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition].key == new[newItemPosition].key

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
                old[oldItemPosition] == new[newItemPosition]

    }
}
