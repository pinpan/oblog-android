package com.applego.oblog.ui

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.applego.oblog.tppwatch.R


/**
 * Adapter for text items Spinner.
 */
class IconAndTextSpinnerAdapter (context: Context, resource: Int, spinner: Spinner, itms: List<String>, tSize: Int = 12)
    : BaseAdapter() {

    private val myContext: Context = context

    // Custom values for the spinner (Service)
    private val items: List<String> = itms

    private val mySpinner: Spinner = spinner
    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private val viewResourceId = resource

    private val textSize: Float = tSize.toFloat()

    override fun getCount(): Int {
        return items?.size ?: 0
    }

    override fun getItem(position: Int): String {
        return items?.get(position)
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position >= count) {
            return super.getDropDownView(position, convertView, parent)
        }

        val view = LayoutInflater.from(myContext).inflate(viewResourceId, parent, false)
        val label = view.findViewById(R.id.title) as TextView
        label.setText(items.get(position))
        label.setTextColor(Color.BLACK)
        label.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize)

        return view
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = super.getDropDownView(position, convertView, parent)
        if (position == mySpinner.selectedItemPosition) {
            view.background = ColorDrawable(myContext.resources.getColor(R.color.colorEULightGrey))
            ((view as LinearLayout).getChildAt(1) as TextView).setTextColor(myContext.resources.getColor(R.color.colorEUFlagYellow))
        }

        return view
    }
}

