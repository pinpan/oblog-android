package com.applego.oblog.tppwatch.notification

import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.FirebaseInstanceIdService
import java.io.DataOutputStream
import java.io.IOException
import java.net.HttpURLConnection
import java.net.MalformedURLException
import java.net.URL


class InstanceIdService : FirebaseInstanceIdService() {
    override fun onTokenRefresh() {
        super.onTokenRefresh()
        val token = FirebaseInstanceId.getInstance().token
        if (!token.isNullOrBlank()) {
            sendToServer(token);
        } else {

        }
    }

    private fun sendToServer(token: String) {
        try {
            val url = URL("https://www.whatsthatlambda.com/store")
            val connection: HttpURLConnection = url.openConnection() as HttpURLConnection
            connection.setDoOutput(true)
            connection.setDoInput(true)
            connection.setRequestMethod("POST")
            val dos = DataOutputStream(connection.getOutputStream())
            dos.writeBytes("token=$token")
            connection.connect()
            if (connection.getResponseCode() === HttpURLConnection.HTTP_OK) {
                // Do whatever you want after the
                // token is successfully stored on the server
            }
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }
}