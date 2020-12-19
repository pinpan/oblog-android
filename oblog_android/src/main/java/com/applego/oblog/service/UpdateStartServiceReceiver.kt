package com.applego.oblog.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent

class UpdateStartServiceReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent?) {
        UpdateUtil.scheduleJob(context);
    }
}