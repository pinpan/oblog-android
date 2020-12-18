package com.applego.oblog.tppwatch.util

/**
 * Extension functions for Fragment.
 */

import androidx.fragment.app.Fragment
import com.applego.oblog.TppWatchApplication

fun Fragment.getViewModelFactory(): ViewModelFactory {
    val repository = (requireContext().applicationContext as TppWatchApplication).tppRepository
    return ViewModelFactory(repository)
}
