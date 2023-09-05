package com.shoykat.displaysms
import android.content.pm.PackageManager
import android.Manifest
import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import androidx.core.content.PermissionChecker.PERMISSION_GRANTED
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat


class MainActivity : AppCompatActivity() {
    //working code

    private val smsPermissionCode = 101
    private lateinit var messageListView: ListView
    private lateinit var messageAdapter: ArrayAdapter<String>
    private lateinit var notificationManager: NotificationManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListView = findViewById(R.id.messageListView)
        messageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        messageListView.adapter = messageAdapter

        requestSmsPermission()

        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Register the SMS receiver
        val smsReceiver = SmsReceiver()
        smsReceiver.setMessageCallback(object : SmsReceiver.MessageCallback {
            override fun onMessageReceived(message: String) {
                messageAdapter.add(message)
                displaySmsPopupAndNotification(message)
            }
        })

        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filter)
    }

    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                smsPermissionCode
            )
        }
    }

    private fun displaySmsPopupAndNotification(message: String) {
        // Show the pop-up dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_popup_layout, null)
        val textViewSmsContent = dialogView.findViewById<TextView>(R.id.textViewSmsContent)

        val dialogBuilder = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(true)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        textViewSmsContent.text = message

        // Use a consistent notification ID (e.g., 1) to replace existing notifications
        val notificationId = 1
        val channelId = "sms_channel" // The same channel ID created in step 1 (if applicable)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channelName = "SMS Channel"
            val importance = NotificationManager.IMPORTANCE_HIGH
            val notificationChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(notificationChannel)
        }

        // Create a RemoteViews object for the custom notification layout
        val customNotificationView = RemoteViews(packageName, R.layout.custom_notification_layout)
        customNotificationView.setTextViewText(R.id.textViewNotificationContent, message)

        // Create the notification
        val notificationBuilder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.baseline_notifications_active_24)
            .setContentTitle("New SMS Message")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setContent(customNotificationView) // Use the custom RemoteViews

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == smsPermissionCode && grantResults.isNotEmpty() && grantResults[0] == PERMISSION_GRANTED) {
            Toast.makeText(this, "SMS Permission Granted", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "SMS Permission Denied", Toast.LENGTH_SHORT).show()
        }
    }
}
/*class MainActivity : AppCompatActivity() {
    //work only clear button

    private val smsPermissionCode = 101
    private lateinit var messageListView: ListView
    private lateinit var messageAdapter: ArrayAdapter<String>
    private lateinit var notificationManager: NotificationManagerCompat
    private val smsMessagesByNumber = mutableMapOf<String, MutableList<String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        messageListView = findViewById(R.id.messageListView)
        messageAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        messageListView.adapter = messageAdapter

        requestSmsPermission()

        notificationManager = NotificationManagerCompat.from(this)

        // Register the SMS receiver
        val smsReceiver = SmsReceiver()
        smsReceiver.setMessageCallback(object : SmsReceiver.MessageCallback {
            override fun onMessageReceived(message: String, phoneNumber: String) {
                // Check if this phone number is already in the map
                if (smsMessagesByNumber.containsKey(phoneNumber)) {
                    // Append the message to the existing list for this number
                    smsMessagesByNumber[phoneNumber]?.add(message)
                } else {
                    // Create a new entry for this phone number
                    val messages = mutableListOf(message)
                    smsMessagesByNumber[phoneNumber] = messages
                }

                updateNotification()
                messageAdapter.add(message)
            }


        })

        val filter = IntentFilter("android.provider.Telephony.SMS_RECEIVED")
        registerReceiver(smsReceiver, filter)

        val clearButton = findViewById<Button>(R.id.clearButton)
        clearButton.setOnClickListener {
            messageAdapter.clear()
        }
    }

    private fun requestSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.RECEIVE_SMS),
                smsPermissionCode
            )
        }
    }

    private fun updateNotification() {
        // Build and display the custom notification here
        // You can use the aggregated messages in smsMessagesByNumber
        // to create a custom notification layout.
        // Set a PendingIntent to open the details activity when clicked.
        // Use NotificationManager to update or create the notification.
    }
}*/
