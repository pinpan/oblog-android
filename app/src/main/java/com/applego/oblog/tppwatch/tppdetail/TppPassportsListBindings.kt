
package com.applego.oblog.tppwatch.tppdetail

import android.widget.ListView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.source.local.EbaPassport
import com.applego.oblog.tppwatch.data.source.local.Tpp
import com.applego.oblog.tppwatch.tpps.TppsAdapter

import timber.log.Timber

/**
 * [BindingAdapter]s for the [EbaPassport]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<EbaPassport.CountryVisa>?) {
    //Timber.w("$$$$$$$$$$$$ The Adapter listView is: " + listView.adapter)

    (listView?.adapter as TppDetailAdapter).submitList(items)
}

@BindingAdapter("app:items")
fun setItemsTo(listView: ListView, items:  List<EbaPassport.CountryVisa>?) {
    (listView?.adapter as TppDetailAdapter).submitList(items)
}
