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
import com.applego.oblog.tppwatch.data.model.EUCountry


/**
 * Adapter for the Countries Spinner list.
 */
class CountriesSpinnerAdapter (context: Context , resource: Int, spinner: Spinner, cntrys: List<EUCountry>, tSize: Int = 12)
    : BaseAdapter() {

    private val myContext: Context = context

    // Custom values for the spinner (Country)
    private val countries: List<EUCountry> = cntrys

    private val mySpinner: Spinner = spinner

    private val textSize: Float = tSize.toFloat()

    /**
     * The resource indicating what views to inflate to display the content of this
     * array adapter.
     */
    private val viewResourceId = resource

    override fun getCount(): Int {
        return countries?.size ?: 0
    }

    override fun getItem(position: Int): EUCountry? {
        return countries?.get(position) ?: null
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position >= count) {
            return super.getDropDownView(position, convertView, parent)
        }

        val view = LayoutInflater.from(myContext).inflate(viewResourceId, parent, false)
        val isoCountryCode = countries.get(position).isoCode
        val imageId = myContext.resources.getIdentifier("ic_flag_flat_" + isoCountryCode.toLowerCase(), "drawable", myContext?.getPackageName())
        val image = view.findViewById(R.id.icon) as ImageView
        image.setImageResource(imageId)

        val label = view.findViewById(R.id.title) as TextView
        val countryName = countries.get(position).countryName
        label.setText(countryName)
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
