package com.applego.oblog.tppwatch.onboarding

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
import com.applego.oblog.tppwatch.databinding.OnboardingFragmentBinding
import com.applego.oblog.tppwatch.util.Event


/**
 * A placeholder fragment containing a simple view.
 */
class OnboardingFragment : Fragment() {

    private lateinit var onboardingViewModel: OnboardingViewModel
    //private val onboardingViewModel = viewModelFactory.get(OnboardingViewModel::class.java) as OnboardingViewModel
    //private val onboardingViewModel = viewModelFactory.get(OnboardingViewModel::class.java) as OnboardingViewModel
    //private lateinit var onboardingViewModel: OnboardingViewModel

    private lateinit var viewDataBinding: OnboardingFragmentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //onboardingViewModel = ViewModelProviders.of(this.activity!!).get(OnboardingViewModel::class.java)
        onboardingViewModel = ViewModelProviders.of(this.requireActivity()).get(OnboardingViewModel::class.java)
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.onboarding_fragment, container, false)

        /*viewDataBinding = OnboardingFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = onboardingViewModel
        }*/
        viewDataBinding = OnboardingFragmentBinding.inflate(inflater, container, false).apply {
            viewmodel = onboardingViewModel
        }

        val titleView: TextView = root.findViewById(R.id.section_label)
        val descView: TextView = root.findViewById(R.id.section_description)

        onboardingViewModel.text.observe(viewLifecycleOwner, Observer<Int> {
            titleView.text = resources.getString(onboardingViewModel.text.value ?: 0)
        })

        onboardingViewModel.desc.observe(viewLifecycleOwner, Observer<Int> {
            descView.text = resources.getString(onboardingViewModel.desc.value ?: 0)
        })

        val imgView: ImageView = root.findViewById(R.id.section_img)
        //val logoView: ImageView = root.findViewById(R.id.section_logo)
        val warningView: TextView = root.findViewById(R.id.section_warning)
        onboardingViewModel.image.observe(viewLifecycleOwner, Observer<Int> {
            imgView.setBackgroundResource(onboardingViewModel.image.value ?: 0)

            imgView.visibility = if (onboardingViewModel.isLastPage()) View.GONE else View.VISIBLE
            //logoView.visibility = if (onboardingViewModel.isLastPage()) View.VISIBLE else View.GONE
            warningView.visibility = if (onboardingViewModel.isLastPage()) View.VISIBLE else View.GONE
        })

        onboardingViewModel.onboardingFinishEvent.observe(viewLifecycleOwner, Observer<Event<Boolean>> {event ->
            event.getContentIfNotHandled().let {
                finish(it ?: true)
            }
        })

        val img = root.findViewById(R.id.section_img) as ImageView
        img.setBackgroundResource(onboardingViewModel.bgs.get(requireArguments().getInt(ARG_SECTION_NUMBER)))

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