package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.databinding.TppItemBinding
import kotlinx.android.synthetic.main.tppdetail_frag.view.*
import timber.log.Timber


/**
 * Adapter for the tpp list. Has a reference to the [TppsViewModel] to send actions back to it.
 */
class TppsAdapter(private val viewModel: TppsViewModel, ctx: Context, layoutId: Int) :
    ListAdapter<Tpp, TppsAdapter.ViewHolder>(TppDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
        val isrevoked = item!!.ebaEntity.isRevoked()
        if (isrevoked) {
            Timber.d(item!!.getId() + " is revoked")
        }
        holder.itemView.setBackgroundResource((if (isrevoked) R.color.colorEUOrange else R.color.colorEULightGrey))
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vH = ViewHolder.from(parent)
        val view : LinearLayout = vH.itemView as LinearLayout
        val item = vH.binding.tpp
        if (view != null) {
            view.title?.textSize = 10f
        }
        return vH
    }

    class ViewHolder private constructor(val binding: TppItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TppsViewModel, item: Tpp) {

            binding.viewmodel = viewModel
            binding.tpp = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TppItemBinding.inflate(layoutInflater, parent, false)

                return ViewHolder(binding)
            }
        }
    }
}

/**
 * Callback for calculating the diff between two non-null tppsList in a list.
 *
 * Used by ListAdapter to calculate the minimum number of changes between and old list and a new
 * list that's been passed to `submitList`.
 */
class TppDiffCallback :  DiffUtil.ItemCallback<Tpp>() {
    override fun areItemsTheSame(oldItem: Tpp, newItem: Tpp): Boolean {
        return oldItem.getId() == newItem.getId()
    }

    override fun areContentsTheSame(oldItem: Tpp, newItem: Tpp): Boolean {
        return oldItem.equals(newItem)
    }
}
