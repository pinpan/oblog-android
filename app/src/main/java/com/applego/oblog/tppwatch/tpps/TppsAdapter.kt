/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.applego.oblog.tppwatch.tpps

import android.R
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.databinding.TppItemBinding
import kotlinx.android.synthetic.main.tpp_item.view.*


/**
 * Adapter for the tpp list. Has a reference to the [TppsViewModel] to send actions back to it.
 */
class TppsAdapter(private val viewModel: TppsViewModel, ctx: Context, layoutId: Int) :
    ListAdapter<Tpp, TppsAdapter.ViewHolder>(TppDiffCallback()) {
    //ArrayAdapter<Tpp>(ctx, layoutId) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vH = ViewHolder.from(parent)
        val view : LinearLayout = vH.itemView as LinearLayout
        if (view != null) {
            view.title_text.textSize = 10f
        }
        return vH
    }

/*

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val rowView = convertView ?: inflater.inflate(R.layout.simple_expandable_list_item_2, parent, false)

        val textView = rowView.findViewById(R.id.title_text) as TextView
        val imageView: ImageView = rowView.findViewById(R.id.icon) as ImageView
        textView.setText(values.get(position))
        // Change the icon for Windows and iPhone

        return rowView
    }
*/

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
class TppDiffCallback : DiffUtil.ItemCallback<Tpp>() {
    override fun areItemsTheSame(oldItem: Tpp, newItem: Tpp): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Tpp, newItem: Tpp): Boolean {
        return oldItem == newItem
    }
}
