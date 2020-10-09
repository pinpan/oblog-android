package com.applego.oblog.tppwatch.util

import android.content.Context
import android.content.res.TypedArray
import com.applego.oblog.tppwatch.R
import timber.log.Timber

class ResourcesUtils {

    companion object {
        fun getActualEnvironmentForActivity(ctx: Context, envName: String) : Array<String> {
            val ta: TypedArray = ctx.resources.obtainTypedArray(R.array.environments)
            val envsArray: Array<Array<String>?> = arrayOfNulls<Array<String>>(ta.length())
            for (i in 0 until ta.length()) {
                val id: Int = ta.getResourceId(i, 0)
                if (id > 0) {
                    envsArray[i] = ctx.resources.getStringArray(id)
                    if (envName.toUpperCase().equals(envsArray[i]?.get(0))) {
                        return envsArray[i]!!
                    }
                } else {
                    Timber.w("Negative ID for resource array signals that there is something wrong with the resources XML")
                }
            }
            ta.recycle() // Important!

            return emptyArray()
        }
    }
}