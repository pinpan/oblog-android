package com.applego.oblog.tppwatch.onboarding

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.onboarding.ui.main.OnboardingViewModel
import com.applego.oblog.tppwatch.onboarding.ui.main.SectionsPagerAdapter
import com.google.android.material.snackbar.Snackbar

class OnboardingActivity : AppCompatActivity() {

    lateinit var  viewPager: ViewPager
    lateinit var  viewModel: OnboardingViewModel

    private lateinit var activeIndicator: ImageView

    private val indicatorViewes = ArrayList<ImageView>()

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("ACTIVE_ONBOARDING_PAGE", viewModel.index.value ?: 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)

        findViewById<Button>(R.id.intro_btn_next)?.let {
            it.setOnClickListener { view ->
                Snackbar.make(it, "Introduction Next", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                viewModel.nextPage()
            }
        }

        findViewById<Button>(R.id.intro_btn_prev)?.let {
            it.setOnClickListener { view ->
                Snackbar.make(it, "Introduction Prev", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()
                viewModel.prevPage()
            }
        }

        val N : Int = ((viewPager.adapter?.count) ?: 0)-1
        for (n in 0..N) {
            val indicator: ImageView = window.decorView.findViewWithTag(resources.getString(R.string.tag_intro_indicator) + n)
            indicatorViewes.add(indicator)
        }

        val curModelPage = viewModel.index.value ?: 0
        activeIndicator = indicatorViewes.get(curModelPage)

        viewModel.index.observe(this, Observer<Int> {
            onPageIndexChanged(it)
        })
    }

    fun onPageIndexChanged(pageNo: Int) {
        activeIndicator.background = ContextCompat.getDrawable(this, R.color.colorGrey)
        activeIndicator = indicatorViewes.get(pageNo)
        activeIndicator.background = ContextCompat.getDrawable(this, R.color.colorDarkGrey)
        findViewById<Button>(R.id.intro_btn_next)?.setEnabled(pageNo < (viewPager.adapter?.count ?: 0)-1)
        findViewById<Button>(R.id.intro_btn_prev)?.setEnabled(pageNo > 0)
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
}