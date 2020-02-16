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
package com.applego.oblog.tppwatch.tppdetail

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.source.local.EbaPassport
import com.applego.oblog.tppwatch.databinding.TppPassportBinding


/**
 * Adapter for the tpp details. Has a reference to the [TppDetailViewModel] to send actions back to it.
 */
class TppDetailAdapter(private val viewModel: TppDetailViewModel, ctx: Context, layoutId: Int) :
    ListAdapter<EbaPassport.CountryVisa, TppDetailAdapter.ViewHolder>(TppDiffCallback()) {

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = getItem(position)

        holder.bind(viewModel, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val vH = ViewHolder.from(parent)
        val view = vH.itemView
        if (view != null) {
            //view.passtpp_title_text?.textSize = 10f
        }
        return vH
    }

    class ViewHolder private constructor(val binding: TppPassportBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(viewModel: TppDetailViewModel, item: EbaPassport.CountryVisa) {

            binding.viewmodel = viewModel
            binding.countryVisa = item
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
class TppDiffCallback : DiffUtil.ItemCallback<EbaPassport.CountryVisa>() {
    override fun areItemsTheSame(oldItem: EbaPassport.CountryVisa, newItem: EbaPassport.CountryVisa): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(oldItem: EbaPassport.CountryVisa, newItem: EbaPassport.CountryVisa): Boolean {
        return oldItem == newItem
    }
}
