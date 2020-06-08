package com.applego.oblog.tppwatch.onboarding.ui.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.util.EventObserver
import com.google.android.material.snackbar.Snackbar

/**
 * A placeholder fragment containing a simple view.
 */
class OnboardingFragment : Fragment() {

    private lateinit var viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java).apply {
            setIndex(arguments?.getInt(ARG_SECTION_NUMBER) ?: 1)
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        setupNavigation()
    }

    private fun setupNavigation() {
        viewModel.onboardingFinishEvent.observe(this, EventObserver {
            activity?.finish()
        })
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

        val skip: Button = root.findViewById(R.id.intro_btn_finish)
        skip.setOnClickListener { view ->
            Snackbar.make(view, "Introduction skipped", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val finish: Button = root.findViewById(R.id.intro_btn_finish)
        finish.setOnClickListener { view ->
            Snackbar.make(view, "Introduction finished", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            viewModel.finishOnboarding()
        }

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