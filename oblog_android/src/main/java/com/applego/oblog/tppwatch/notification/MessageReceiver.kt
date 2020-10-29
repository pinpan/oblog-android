package com.applego.oblog.tppwatch.notification

import android.R
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.applego.oblog.tppwatch.tpps.TppsActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class MessageReceiver: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage?) {
        super.onMessageReceived(remoteMessage)

        val title: String = remoteMessage.getNotification().getTitle(); //remoteMessage.getData().get("title");
        val message: String =  remoteMessage.getNotification().getBody(); //remoteMessage.getData().get("body");

        showNotifications(title, message);
    }

    private fun showNotifications(title: String, msg: String) {
        val i = Intent(this, TppsActivity::class.java)
        val pendingIntent
                = PendingIntent.getActivity(this, REQUEST_CODE,
                                            i, PendingIntent.FLAG_UPDATE_CURRENT)

        val notification: RemoteMessage.Notification = RemoteMessage.Builder(this)
                .setContentText(msg)
                .setContentTitle(title)
                .setContentIntent(pendingIntent)
                .setSmallIcon(R.drawable.ic_launcher_round)
                .build()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(NOTIFICATION_ID, notification)
    }
}