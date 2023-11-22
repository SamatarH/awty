package edu.uw.ischool.samatar.awty

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.telephony.SmsManager

class MessageService : Service() {
    private lateinit var message: String
    private lateinit var phoneNumber: String
    private var minutes: Int = 0
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            message = intent.getStringExtra("message") ?: ""
            phoneNumber = intent.getStringExtra("phoneNumber") ?: ""
            minutes = intent.getIntExtra("minutes", 0)

            startSendingMessages()
        }

        return START_STICKY
    }

    private fun startSendingMessages() {
        runnable = object : Runnable {
            override fun run() {
                sendSMSMessage(phoneNumber, "$message: Are we there yet?")
                handler.postDelayed(this, (minutes * 60 * 1000).toLong())
            }
        }

        handler.post(runnable)
    }

    private fun sendSMSMessage(phoneNumber: String, message: String) {
        val smsManager = SmsManager.getDefault()
        smsManager.sendTextMessage(phoneNumber, null, message, null, null)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}
