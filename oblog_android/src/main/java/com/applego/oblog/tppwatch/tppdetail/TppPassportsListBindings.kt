
package com.applego.oblog.tppwatch.tppdetail

import android.widget.ListView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.applego.oblog.tppwatch.data.model.EbaPassport

/**
 * [BindingAdapter]s for the [EbaPassport]s list.
 */
@BindingAdapter("app:items")
fun setItems(listView: RecyclerView, items: List<EbaPassport.ServiceVisa>?) {
    //Timber.w("$$$$$$$$$$$$ The Adapter listView is: " + listView.adapter)

    (listView?.adapter as TppDetailAdapter).submitList(items)
}

@BindingAdapter("app:items")
fun setItemsTo(listView: ListView, items:  List<EbaPassport.ServiceVisa>?) {
    (listView?.adapter as TppDetailAdapter).submitList(items)
}
