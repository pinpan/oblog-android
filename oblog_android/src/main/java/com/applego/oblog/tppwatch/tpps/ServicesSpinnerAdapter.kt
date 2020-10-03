package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EbaService


/**
 * Adapter for the PSD2 Servicess Spinner list.
 */
class ServicesSpinnerAdapter (context: Context, resource: Int, spinner: Spinner, srvcs: List<EbaService>)
    : BaseAdapter() {

    private val myContext: Context = context

    // Custom values for the spinner (Service)
    private val services: List<EbaService> = srvcs

    private val mySpinner: Spinner = spinner
    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private val viewResourceId = resource

    override fun getCount(): Int {
        return services?.size ?: 0
    }

    override fun getItem(position: Int): EbaService? {
        return services?.get(position) ?: null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position >= count) {
            return super.getDropDownView(position, convertView, parent)
        }

        val view = LayoutInflater.from(myContext).inflate(viewResourceId, parent, false)
        //val serviceCode = services.get(position).code

        val label = view.findViewById(R.id.title) as TextView
        val serviceName = services.get(position).shortDescription
        label.setText(serviceName)
        label.setTextColor(Color.BLACK)

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

