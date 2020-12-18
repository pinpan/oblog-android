package com.applego.oblog.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.widget.Toast

class NetworkChangeReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val status: String = "Network state changed"
        // TODO: NetworkUtil.getConnectivityStatusString(context)
        /*Above line will return the status of wifi */

        Toast.makeText(context, status, Toast.LENGTH_LONG).show()
    }
}