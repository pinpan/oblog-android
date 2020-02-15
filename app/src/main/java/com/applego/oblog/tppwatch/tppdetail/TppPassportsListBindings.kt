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

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.source.local.EbaPassport

import timber.log.Timber

/**
 * [BindingAdapter]s for the [EbaPassport]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<EbaPassport.CountryVisa>) {
    Timber.w("$$$$$$$$$$$$ The Adapter listView is: " + listView.adapter)

    (listView?.adapter as TppDetailAdapter).submitList(items)
}
