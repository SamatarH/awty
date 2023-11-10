package edu.uw.ischool.samatar.awty

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private lateinit var messageEditText: EditText
    private lateinit var phoneNumberEditText: EditText
    private lateinit var minutesEditText: EditText
    private lateinit var startStopButton: Button
    private var isServiceRunning = false

    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val message = intent?.getStringExtra("message")
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageEditText = findViewById(R.id.messageEditText)
        phoneNumberEditText = findViewById(R.id.phoneNumberEditText)
        minutesEditText = findViewById(R.id.minutesEditText)
        startStopButton = findViewById(R.id.startStopButton)

        startStopButton.setOnClickListener {
            toggleService()
        }

        registerReceiver(receiver, IntentFilter("SHOW_TOAST"))
    }

    private fun toggleService() {
        if (!isServiceRunning) {
            val message = messageEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val minutes = minutesEditText.text.toString().toIntOrNull()

            if (message.isNotEmpty() && phoneNumber.isNotEmpty() && minutes != null && minutes > 0) {
                startService(message, minutes)
                startStopButton.text = "Stop"
            }
        } else {
            stopService()
            startStopButton.text = "Start"
        }
    }

    private fun startService(message: String, minutes: Int) {
        val serviceIntent = Intent(this, MessageService::class.java)
        serviceIntent.putExtra("message", message)
        serviceIntent.putExtra("minutes", minutes)
        serviceIntent.putExtra("userInput", messageEditText.text.toString())
        startService(serviceIntent)
        isServiceRunning = true
    }

    private fun stopService() {
        val serviceIntent = Intent(this, MessageService::class.java)
        stopService(serviceIntent)
        isServiceRunning = false
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }
}
