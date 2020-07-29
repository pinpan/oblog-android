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
import com.applego.oblog.tppwatch.onboarding.OnboardingActivity
import com.applego.oblog.tppwatch.util.Event


/**
 * A placeholder fragment containing a simple view.
 */
class OnboardingFragment : Fragment() {

    private lateinit var viewModel: OnboardingViewModel

    // var img: ImageView? = null
    var bgs: IntArray = intArrayOf(R.drawable.oblog_onboarding_1, R.drawable.oblog_onboarding_2, R.drawable.oblog_onboarding_3)
    var lbls: IntArray = intArrayOf(R.string.onboarding_label_1, R.string.onboarding_label_2, R.string.onboarding_label_3)
    var dscs: IntArray = intArrayOf(R.string.onboarding_description_1, R.string.onboarding_description_2, R.string.onboarding_description_3)

/*
    var color1 = ContextCompat.getColor(this.activity, R.color.cyan)
    var color2 = ContextCompat.getColor(this, R.color.orange)
    var color3 = ContextCompat.getColor(this, R.color.green)
    var colorList = intArrayOf(color1, color2, color3)
*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this.activity!!).get(OnboardingViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.onboarding_fragment, container, false)
        val textView: TextView = root.findViewById(R.id.section_label)
        val descView: TextView = root.findViewById(R.id.section_description)

        viewModel.text.observe(this, Observer<String> {
            textView.text = resources.getString(lbls.get(arguments!!.getInt(ARG_SECTION_NUMBER)))
        })

        viewModel.desc.observe(this, Observer<String> {
            descView.text = resources.getString(dscs.get(arguments!!.getInt(ARG_SECTION_NUMBER)))
        })

        viewModel.onboardingFinishEvent.observe(this, Observer<Event<Boolean>> {event ->
            event.getContentIfNotHandled().let {
                finish(it ?: true)
            }
        })

        val img = root.findViewById(R.id.section_img) as ImageView
        img.setBackgroundResource(bgs.get(arguments!!.getInt(ARG_SECTION_NUMBER)))

        /*val sectionLabel = root.findViewById(R.id.section_label) as TextView
        sectionLabel.setText(lbls.get(arguments!!.getInt(ARG_SECTION_NUMBER)))

        val sectionDescription = root.findViewById(R.id.section_description) as TextView
        sectionDescription.setText(dscs.get(arguments!!.getInt(ARG_SECTION_NUMBER)))*/

        return root
    }

    fun finish(regularFinish: Boolean) {
        (this.activity as OnboardingActivity)?.finish(regularFinish)
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