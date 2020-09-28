package com.applego.oblog.tppwatch.tpps

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.data.model.EUCountry


/**
 * Adapter for the Countries Spinner list.
 */
class CountriesSpinnerAdapter (context: Context , resource: Int, cntrys: List<EUCountry>)
    : BaseAdapter() {

    private val myContext: Context = context

    // Your custom values for the spinner (Country)
    private val countries: List<EUCountry>? = cntrys

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

//    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
//        return super.getView(position, convertView, parent)
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        if (position >= count) {
            return super.getDropDownView(position, convertView, parent)
        }

        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        val view = LayoutInflater.from(myContext).inflate(viewResourceId, parent, false)//super.getView(position, convertView, parent!!)// as TextView
        //val relLay = (view as LinearLayout).getChildAt(0) as RelativeLayout

        val isoCountryCode = myContext.resources.getStringArray(R.array.eu_countries_iso)!![position]
        val countryIsoId = myContext.resources.getIdentifier(isoCountryCode, "string", myContext?.getPackageName())

        val imageId = myContext.resources.getIdentifier("ic_flag_flat_" + isoCountryCode, "drawable", myContext?.getPackageName())
        val image = view.findViewById(R.id.country_flag) as ImageView
        image.setImageResource(imageId)


        val label = view.findViewById(R.id.country_name) as TextView
        val countryName = myContext.resources.getStringArray(R.array.eu_countries)!![position]
        label.setText(if (position != 0) countryName else "N/A")
        label.setTextColor(Color.BLACK)
        //label.setText(countries?.get(position)?.name)

        // And finally return your dynamic (or custom) view for each spinner item
        return view
    }

    // And here is when the "chooser" is popped up
    // Normally is the same view, but you can customize it if you want
    override fun getDropDownView(position: Int, convertView: View?,
                                 parent: ViewGroup?): View? {
        val view = super.getDropDownView(position, convertView, parent)
        //val relLay = (view as LinearLayout).getChildAt(0) as RelativeLayout
/*

        val label = view.findViewById(R.id.country_name) as TextView
        val countryName = myContext.resources.getStringArray(R.array.eu_countries)!![position]
        label.setTextColor(Color.BLACK)
        label.setText(countries?.get(position)?.name)
*/

        return view
    }
}

