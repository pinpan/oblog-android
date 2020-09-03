package com.applego.oblog.tppwatch.tppdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.model.App
import com.applego.oblog.tppwatch.databinding.AppItemBinding
import kotlinx.android.synthetic.main.tppdetail_frag.view.*


/**
 * Adapter for the apps list. Has a reference to the [AppsViewModel] to send actions back to it.
 */
class AppsAdapter(private val viewModel: AppsViewModel, ctx: Context, layoutId: Int) :
    ListAdapter<App, AppsAdapter.ViewHolder>(AppDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vH = ViewHolder.from(parent)
        val view : LinearLayout = vH.itemView as LinearLayout
        if (view != null) {
            view.title?.textSize = 11f
        }
        return vH
    }

    class ViewHolder private constructor(val binding: AppItemBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: AppsViewModel, item: App) {

            binding.viewmodel = viewModel
            binding.app = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = AppItemBinding.inflate(layoutInflater, parent, false)

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
class AppDiffCallback :  DiffUtil.ItemCallback<App>() {
    override fun areItemsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: App, newItem: App): Boolean {
        return oldItem.equals(newItem)
    }
}
