package com.applego.oblog.tppwatch.util

import com.applego.oblog.tppwatch.tpps.TppsViewModel
import kotlinx.coroutines.runBlocking

/**
 * A blocking version of TppsRepository.saveTpp to minimize the number of times we have to
 * explicitly add <code>runBlocking { ... }</code> in our tests
 */
fun TppsViewModel.loadTppsBlocking(forceUpdate: Boolean) = runBlocking {
    if (forceUpdate) {
        this@loadTppsBlocking.syncEbaDirectory()
    } else {
        this@loadTppsBlocking.loadTpps()
    }
}
