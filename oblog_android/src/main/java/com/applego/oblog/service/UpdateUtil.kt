package com.applego.oblog.service

import android.app.job.JobInfo
import android.app.job.JobScheduler
import android.content.ComponentName
import android.content.Context


object UpdateUtil {
    fun scheduleJob(context: Context) {
        val serviceComponent = ComponentName(context, UpdateJobService::class.java)
        val builder = JobInfo.Builder(0, serviceComponent)
        builder.setMinimumLatency((1 * 1000).toLong()) // wait at least
        builder.setOverrideDeadline((3 * 1000).toLong()) // maximum delay
        //builder.setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED); // require unmetered network
        //builder.setRequiresDeviceIdle(true); // device should be idle
        //builder.setRequiresCharging(false); // we don't care if the device is charging or not
        val jobScheduler: JobScheduler = context.getSystemService(JobScheduler::class.java)
        jobScheduler.schedule(builder.build())
    }
}