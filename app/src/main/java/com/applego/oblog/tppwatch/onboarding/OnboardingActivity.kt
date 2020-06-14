package com.applego.oblog.tppwatch.onboarding

import android.os.Bundle
import android.view.View
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
import com.applego.oblog.tppwatch.util.Event
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.tppdetail_frag.view.*

class OnboardingActivity : AppCompatActivity() {

    lateinit var  viewPager: ViewPager
    lateinit var  viewModel: OnboardingViewModel

    private lateinit var activeIndicator: ImageView

    private val indicatorViewes = ArrayList<ImageView>()

    /*override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("ACTIVE_ONBOARDING_PAGE", viewModel.index.value ?: 0)
    }*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.onboarding_activity)

        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)

        viewPager = findViewById(R.id.view_pager)
        viewPager.adapter = sectionsPagerAdapter

        viewPager.addOnPageChangeListener(object: ViewPager.SimpleOnPageChangeListener() {

            override public fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                //val colorUpdate = (Int) evaluator . evaluate (positionOffset, colorList[position], colorList[position == 2 ? position : position+1]);
                //viewPager.setBackgroundColor(colorUpdate);
                viewModel.setIndex(position)
            }
        })

        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)
        if (viewModel.index == null) {
            viewModel.setIndex(0)
        }

        findViewById<Button>(R.id.intro_btn_next)?.let {
            it.setOnClickListener { view ->
                /*Snackbar.make(it, "Introduction Next", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()*/
                viewModel.nextPage()
            }
        }

        findViewById<Button>(R.id.intro_btn_prev)?.let {
            it.setOnClickListener { view ->
                /*Snackbar.make(it, "Introduction Prev", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show()*/
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

    override fun onStart() {
        super.onStart()
        if (viewModel.index.value == null) {
            viewModel.setIndex(0)
        }
    }

    override fun onResume() {
        super.onResume()
        if (viewModel.index.value == null) {
            viewModel.setIndex(0)
        }
    }

    fun onPageIndexChanged(pageNo: Int) {
        activeIndicator.background = ContextCompat.getDrawable(this, R.color.colorGrey)
        activeIndicator = indicatorViewes.get(pageNo)
        activeIndicator.background = ContextCompat.getDrawable(this, R.color.colorDarkGrey)
        val btnNext = findViewById<Button>(R.id.intro_btn_next)
        if (btnNext != null) {
            if (pageNo == (viewPager.adapter?.count ?: 0)-1) {
                btnNext.text  = resources.getString(R.string.label_onbording_start_using)
            } else {
                btnNext.text  = resources.getString(R.string.label_intro_next)
            }
            /*btnNext.setEnabled(pageNo < (viewPager.adapter?.count ?: 0)-1)*/
        }
        val btnPrev = findViewById<Button>(R.id.intro_btn_prev)
        if (btnPrev != null) {
            btnPrev.setEnabled(pageNo < (viewPager.adapter?.count ?: 0))
            btnPrev.visibility = if (pageNo > 0) android.view.View.VISIBLE else View.GONE
        }
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