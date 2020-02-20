package com.applego.oblog.tppwatch.util

import android.content.Context
import android.util.AttributeSet
import android.widget.AutoCompleteTextView
import android.widget.SearchView


class EmptySubmitSearchView : SearchView {

    /*
* Created by: Jens Klingenberg (jensklingenberg.de)
* GPLv3
*
*   //This SearchView gets triggered even when the query submit is empty
*
* */

    internal var mSearchSrcTextView: AutoCompleteTextView? = null
    internal var listener: OnQueryTextListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun setOnQueryTextListener(listener: OnQueryTextListener?) {
        super.setOnQueryTextListener(listener)
        this.listener = listener
        var searchPlateId = context.resources.getIdentifier("android:id/search_src_text", null, null)
        mSearchSrcTextView = this.findViewById(searchPlateId) as AutoCompleteTextView
        mSearchSrcTextView?.setOnEditorActionListener({ textView, i, keyEvent ->
            if (listener != null) {
                listener!!.onQueryTextSubmit(getQuery().toString())
            }
            true
        })
    }
}