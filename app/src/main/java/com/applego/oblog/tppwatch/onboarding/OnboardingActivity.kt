package com.applego.oblog.tppwatch.onboarding

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.onboarding.ui.main.OnboardingViewModel
import com.applego.oblog.tppwatch.onboarding.ui.main.SectionsPagerAdapter
import com.applego.oblog.tppwatch.util.EventObserver
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.onboarding_fragment.*

class OnboardingActivity : AppCompatActivity() {

    lateinit var  viewPager: ViewPager
    lateinit var  viewModel: OnboardingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)

        findViewById<Button>(R.id.intro_btn_next)?.let {
            it.setOnClickListener { view ->
                //viewPager.adapter.viewModel.finishOnboarding()
                viewModel.nextPage()
            }
        }

        findViewById<Button>(R.id.intro_btn_prev)?.let {
            it.setOnClickListener { view ->
                Snackbar.make(it, "Introduction finished", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                viewModel.prevPage()
            }
        }

        findViewById<Button>(R.id.intro_btn_finish)?.let {
            it.setOnClickListener {
                Snackbar.make(it, "Introduction finished", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                finish()
            }
        }
    }

    private fun nextPage() {
        Snackbar.make(this.view, "Introduction next page", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        viewModel.finishOnboarding()
    }

    private fun prevPage() {
        Snackbar.make(this.view, "Introduction prev page", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
    }

    override fun onBackPressed() {
        if (viewPager.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem -= 1
        }
    }

   /*
    internal class AwesomeButtonClick : View.OnClickListener {
        override fun onClick(v: View?) {
            viewPager.viewModel.finishOnboarding()
        }
    }
*/
    /*
    override fun onCreateView(parent: View?, name: String, context: Context, attrs: AttributeSet): View? {

        val view = super.onCreateView(parent, name, context, attrs)

        val skip : Button? = view?.findViewById(R.id.intro_btn_skip)
        skip?.setOnClickListener { view ->
            Snackbar.make(view, "Introduction skipped", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
        }

        val finish : Button? = view?.findViewById(R.id.intro_btn_finish)
        finish?.setOnClickListener { view ->
            Snackbar.make(view, "Introduction finished", Snackbar.LENGTH_LONG)
                    .setAction("Action", null).show()
            //viewPager.viewModel.finishOnboarding()
        }

        return view
    }
*/
}