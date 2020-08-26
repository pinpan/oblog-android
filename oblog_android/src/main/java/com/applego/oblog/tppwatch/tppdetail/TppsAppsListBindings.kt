package com.applego.oblog.tppwatch.tppdetail

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.model.App

/**
 * [BindingAdapter]s for the [Tpp]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<App>) {
    (listView.adapter as AppsAdapter).submitList(items)
}
