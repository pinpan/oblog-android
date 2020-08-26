package com.applego.oblog.tppwatch.tpps

import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.model.Tpp

/**
 * [BindingAdapter]s for the [Tpp]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<Tpp>) {
    (listView.adapter as TppsAdapter).submitList(items)
}
