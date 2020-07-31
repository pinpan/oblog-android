package com.applego.oblog.tppwatch.onboarding

import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.viewpager.widget.ViewPager
import com.applego.oblog.tppwatch.R
import com.applego.oblog.tppwatch.onboarding.ui.main.OnboardingViewModel
import com.applego.oblog.tppwatch.onboarding.ui.main.SectionsPagerAdapter
import com.applego.oblog.tppwatch.tpps.TppsActivity

class OnboardingActivity : AppCompatActivity() {

    lateinit var  viewPager: ViewPager
    lateinit var  viewModel: OnboardingViewModel

    private lateinit var activeIndicator: ImageView

    private val indicatorViewes = ArrayList<ImageView>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Toast.makeText(this@OnboardingActivity, "Run only once", Toast.LENGTH_LONG).show()
        viewModel = ViewModelProviders.of(this).get(OnboardingViewModel::class.java)

        val sharedPerfs = PreferenceManager.getDefaultSharedPreferences(this)
        var isFirstRun = sharedPerfs.getBoolean("isFirstRun", true)
        var shouldShowIntro = sharedPerfs.getBoolean("show_intro", false)

            /*Handler().post(object : Runnable {

                override fun run(): Unit {
                    startActivity(Intent(this@OnboardingActivity, TppsActivity::class.java))
                }
            })*/
        setContentView(R.layout.onboarding_activity)
        viewPager = findViewById(R.id.view_pager)
        val sectionsPagerAdapter = SectionsPagerAdapter(this, supportFragmentManager)
        viewPager.adapter = sectionsPagerAdapter

        if (isFirstRun || shouldShowIntro) {

            viewPager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
                override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                    viewModel.setIndex(position)
                }
            })

            findViewById<ImageButton>(R.id.intro_btn_next)?.let {
                it.setOnClickListener { view ->
                    viewPager.setCurrentItem(viewModel.nextPage(), true);
                }
            }

            findViewById<Button>(R.id.intro_btn_finish)?.let {
                it.setOnClickListener { view ->
                    viewModel.finishOnboarding(true)
                }
            }

            findViewById<Button>(R.id.intro_btn_skip)?.let {
                it.setOnClickListener { view ->
                    viewModel.finishOnboarding(false)
                }
            }

            val N: Int = ((viewPager.adapter?.count) ?: 0) - 1
            for (n in 0..N) {
                val indicator: ImageView = window.decorView.findViewWithTag(resources.getString(R.string.tag_intro_indicator) + n)
                indicatorViewes.add(indicator)
            }
            activeIndicator = indicatorViewes.get(viewModel.index.value ?: 0)

            viewModel.index.observe(this, Observer<Int> {
                onPageIndexChanged(it)
            })

            viewModel.onboardingFinishEvent.observe(this, Observer {
                finish()
            })
        } else {
            finish()
        }
    }

    fun finish(regularFinish: Boolean) {
        finish()
    }

    override fun finish() {
        if (viewPager.currentItem == viewModel.pageCount) {
            val sharedPerfs = PreferenceManager.getDefaultSharedPreferences(this)

            val editor = sharedPerfs.edit()
            editor.putBoolean("isFirstRun", false)
            editor.commit()
        }

        // if (TargetApi >= 16)
        //   finishAffinity()
        val intent = Intent(this@OnboardingActivity, TppsActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent)

        super.finish()
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

        val isLastPage = !(pageNo < (viewPager.adapter?.count ?: 0) - 1)

        val btnSkip = findViewById<Button>(R.id.intro_btn_skip)
        if (btnSkip != null) {
            btnSkip.visibility = if (!isLastPage) View.VISIBLE else View.GONE
        }

        val btnNext = findViewById<ImageButton>(R.id.intro_btn_next)
        if (btnNext != null) {
            btnNext.visibility = if (!isLastPage) View.VISIBLE else View.GONE
        }

        val btnFinish = findViewById<Button>(R.id.intro_btn_finish)
        if (btnFinish != null) {
                btnFinish.visibility = if (isLastPage)  View.VISIBLE else View.GONE
        }
    }

    override fun onBackPressed() {
        if (viewPager?.currentItem == 0) {
            // If the user is currently looking at the first step, allow the system to handle the
            // Back button. This calls finish() on this activity and pops the back stack.
            super.onBackPressed()
        } else {
            // Otherwise, select the previous step.
            viewPager.currentItem -= 1
        }
    }
}