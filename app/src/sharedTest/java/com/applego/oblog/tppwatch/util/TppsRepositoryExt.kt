package com.applego.oblog.tppwatch.util

import com.applego.oblog.tppwatch.data.model.Tpp
import com.applego.oblog.tppwatch.data.repository.TppsRepository
import kotlinx.coroutines.runBlocking

/**
 * A blocking version of TppsRepository.saveTpp to minimize the number of times we have to
 * explicitly add <code>runBlocking { ... }</code> in our tests
 */
fun TppsRepository.saveTppBlocking(tpp: Tpp) = runBlocking {
    this@saveTppBlocking.saveTpp(tpp)
}

fun TppsRepository.getTppsBlocking(forceUpdate: Boolean) = runBlocking {
    this@getTppsBlocking.getAllTpps(forceUpdate)
}

fun TppsRepository.deleteAllTppsBlocking() = runBlocking {
    this@deleteAllTppsBlocking.deleteAllTpps()
}
