package com.shoykat.displaysms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage



class SmsReceiver : BroadcastReceiver() {

    private var messageCallback: MessageCallback? = null

    fun setMessageCallback(callback: MessageCallback) {
        messageCallback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            if (bundle != null) {
                val pdus = bundle["pdus"] as Array<*>
                for (pdu in pdus) {
                    val message = SmsMessage.createFromPdu(pdu as ByteArray)
                    val sender = message.originatingAddress
                    val messageBody = message.messageBody
                    val smsMessage = "From: $sender\nMessage: $messageBody"
                    messageCallback?.onMessageReceived(smsMessage)
                }
            }
        }
    }

    interface MessageCallback {
        fun onMessageReceived(message: String)
    }
}
