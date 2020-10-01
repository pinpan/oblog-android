package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry


/**
 * Adapter for the Countries Spinner list.
 */
class CountriesSpinnerAdapter (context: Context , resource: Int, spinner: Spinner, cntrys: List<EUCountry>)
    : BaseAdapter() {

    private val myContext: Context = context

    // Custom values for the spinner (Country)
    private val countries: List<EUCountry> = cntrys

    private val mySpinner: Spinner = spinner
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

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        val view = LayoutInflater.from(myContext).inflate(viewResourceId, parent, false)
        val isoCountryCode = countries.get(position).isoCode
        val imageId = myContext.resources.getIdentifier("ic_flag_flat_" + isoCountryCode.toLowerCase(), "drawable", myContext?.getPackageName())
        val image = view.findViewById(R.id.country_flag) as ImageView
        image.setImageResource(imageId)

        val label = view.findViewById(R.id.country_name) as TextView
        val countryName = countries.get(position).countryName
        label.setText(countryName)
        label.setTextColor(Color.BLACK)

        return view
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup?): View? {
        val view = super.getDropDownView(position, convertView, parent)

        if (position == (mySpinner).selectedItemPosition) {
            view.background = ColorDrawable(myContext.resources.getColor(R.color.colorEULightGrey))
            //view.setTextColor(resources.getColor(R.color.colorEUFlagYellow))
            ((view as LinearLayout).getChildAt(1) as TextView).setTextColor(myContext.resources.getColor(R.color.colorEUFlagYellow))
        }
/*

        val label = view.findViewById(R.id.country_name) as TextView
        val countryName = myContext.resources.getStringArray(R.array.eu_countries)!![position]
        label.setTextColor(Color.BLACK)
        label.setText(countries?.get(position)?.name)
*/

        return view
    }
}

