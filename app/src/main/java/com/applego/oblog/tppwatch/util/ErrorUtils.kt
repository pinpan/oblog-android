package com.applego.oblog.tppwatch.util

class ErrorUtils {

    companion object {
        suspend public fun parseError(code: Int, message: String): String {

            return code.toString() + "#" + message
        }
    }
}
