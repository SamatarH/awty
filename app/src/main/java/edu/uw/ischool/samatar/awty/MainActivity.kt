package edu.uw.ischool.samatar.awty

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

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
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.SEND_SMS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.SEND_SMS), SEND_SMS_PERMISSION_REQUEST_CODE)
            } else {
                toggleService()
            }
        }

        registerReceiver(receiver, IntentFilter("SHOW_TOAST"))
    }

    private fun toggleService() {
        if (!isServiceRunning) {
            val message = messageEditText.text.toString()
            val phoneNumber = phoneNumberEditText.text.toString()
            val minutes = minutesEditText.text.toString().toIntOrNull()

            if (message.isNotEmpty() && phoneNumber.isNotEmpty() && minutes != null && minutes > 0) {
                startService(message, phoneNumber, minutes)
                startStopButton.text = "Stop"
            }
        } else {
            stopService()
            startStopButton.text = "Start"
        }
        isServiceRunning = !isServiceRunning
    }

    private fun startService(message: String, phoneNumber: String, minutes: Int) {
        val serviceIntent = Intent(this, MessageService::class.java)
        serviceIntent.putExtra("message", message)
        serviceIntent.putExtra("phoneNumber", phoneNumber)
        serviceIntent.putExtra("minutes", minutes)
        startService(serviceIntent)
    }

    private fun stopService() {
        val serviceIntent = Intent(this, MessageService::class.java)
        stopService(serviceIntent)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SEND_SMS_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                toggleService()
            } else {
                Toast.makeText(this, "SMS permission is required to send messages", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(receiver)
    }

    companion object {
        private const val SEND_SMS_PERMISSION_REQUEST_CODE = 101
    }
}
