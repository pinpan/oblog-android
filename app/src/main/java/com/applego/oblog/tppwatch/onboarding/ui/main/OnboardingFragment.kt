package com.applego.oblog.tppwatch.onboarding.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.applego.oblog.tppwatch.R


/**
 * A placeholder fragment containing a simple view.
 */
class OnboardingFragment : Fragment() {

    private lateinit var viewModel: OnboardingViewModel

    // var img: ImageView? = null
    var bgs: IntArray = intArrayOf(R.drawable.ic_flight_24dp, R.drawable.ic_mail_24dp, R.drawable.ic_explore_24dp)

/*
    var color1 = ContextCompat.getColor(this.activity, R.color.cyan)
    var color2 = ContextCompat.getColor(this, R.color.orange)
    var color3 = ContextCompat.getColor(this, R.color.green)
    var colorList = intArrayOf(color1, color2, color3)
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(OnboardingViewModel::class.java)/*.apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 0)
        }*/
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.onboarding_fragment, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)

        viewModel.text.observe(this, Observer<String> {
            textView.text = it
        })


        val img = root.findViewById(R.id.section_img) as ImageView
        img.setBackgroundResource(bgs.get(arguments!!.getInt(ARG_SECTION_NUMBER) - 1))


        return root
    }

    companion object {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private const val ARG_SECTION_NUMBER = "section_number"

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        @JvmStatic
        fun newInstance(sectionNumber: Int): OnboardingFragment {
            return OnboardingFragment().apply {
                arguments = Bundle().apply {
                    putInt(ARG_SECTION_NUMBER, sectionNumber)
                }
            }
        }
    }
}