package com.applego.oblog.tppwatch.tppdetail

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.widget.TooltipCompat
import androidx.core.app.ActivityCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.model.EbaPassport
import com.applego.oblog.tppwatch.databinding.TppPassportBinding
import kotlinx.android.synthetic.main.tppdetail_frag.view.*


/**
 * Adapter for the tpp details. Has a reference to the [TppDetailViewModel] to send actions back to it.
 */
class TppDetailAdapter(private val viewModel: TppDetailViewModel, ctx: Context, layoutId: Int) :
    ListAdapter<EbaPassport.ServiceVisa, TppDetailAdapter.ViewHolder>(TppDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) { // 26
            holder.itemView.setTooltipText(item.serviceDetail)
        } else {
            TooltipCompat.setTooltipText(holder.itemView, "Service description");
            holder.itemView.setOnLongClickListener(object: View.OnLongClickListener {
                override fun onLongClick(v: View) :  Boolean {
                    Toast.makeText(v.getContext(), "My tool-tip text", Toast.LENGTH_SHORT).show()
                    return true
                }
            })
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vH = ViewHolder.from(parent)
        val view : LinearLayout = vH.itemView as LinearLayout
        if (view != null) {
            view.title?.textSize = 12f
        }
        return vH
    }

    class ViewHolder private constructor(val binding: TppPassportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TppDetailViewModel, item: EbaPassport.ServiceVisa) {

            //binding.viewmodel = viewModel
            binding.serviceVisa = item
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): ViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = TppPassportBinding.inflate(layoutInflater, parent, false)

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
class TppDiffCallback : DiffUtil.ItemCallback<EbaPassport.ServiceVisa>() {
    override fun areItemsTheSame(oldItem: EbaPassport.ServiceVisa, newItem: EbaPassport.ServiceVisa): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: EbaPassport.ServiceVisa, newItem: EbaPassport.ServiceVisa): Boolean {
        return oldItem.equals(newItem)
    }
}
