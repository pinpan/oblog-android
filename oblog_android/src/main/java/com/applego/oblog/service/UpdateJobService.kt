package com.applego.oblog.service

import android.app.job.JobParameters
import android.app.job.JobService
import android.content.Intent

class UpdateJobService : JobService() {
    override fun onStartJob(params: JobParameters?): Boolean {
        //TODO("Not yet implemented")
        val service = Intent(applicationContext, UpdateService      ::class.java)
        applicationContext.startService(service)
        UpdateUtil.scheduleJob(applicationContext) // reschedule the job

        return true
    }

    override fun onStopJob(params: JobParameters?): Boolean {
        //TODO("Not yet implemented")
        return true
    }

    /*override fun onBind(intent: Intent): IBinder {
        TODO("Return the communication channel to the service.")
    }*/

}