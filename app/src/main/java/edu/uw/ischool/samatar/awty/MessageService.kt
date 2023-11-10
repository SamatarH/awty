package edu.uw.ischool.samatar.awty

import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.widget.Toast

class MessageService : Service() {
    private lateinit var message: String
    private var minutes: Int = 0
    private lateinit var userInput: String
    private lateinit var broadcastIntent: Intent
    private val handler = Handler(Looper.getMainLooper())
    private lateinit var runnable: Runnable

    override fun onBind(intent: Intent?): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (intent != null) {
            message = intent.getStringExtra("message") ?: ""
            minutes = intent.getIntExtra("minutes", 0)
            userInput = intent.getStringExtra("userInput") ?: ""

            startSendingMessages()
        }

        return START_STICKY
    }

    private fun startSendingMessages() {
        runnable = object : Runnable {
            override fun run() {
                val toastMessage = "$message: $userInput"
                showToast(toastMessage)
                handler.postDelayed(this, (minutes * 60 * 1000).toLong())
            }
        }

        handler.post(runnable)
    }

    private fun showToast(message: String) {
        broadcastIntent = Intent("SHOW_TOAST")
        broadcastIntent.putExtra("message", message)
        sendBroadcast(broadcastIntent)
    }

    override fun onDestroy() {
        handler.removeCallbacks(runnable)
        super.onDestroy()
    }
}
