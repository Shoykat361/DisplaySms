package com.shoykat.displaysms

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.telephony.SmsMessage

/*class SmsReceiver : BroadcastReceiver() {
    //handle only clear button

    interface MessageCallback {
        fun onMessageReceived(message: String, phoneNumber: String)
    }

    private var messageCallback: MessageCallback? = null

    fun setMessageCallback(callback: MessageCallback) {
        messageCallback = callback
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == "android.provider.Telephony.SMS_RECEIVED") {
            val bundle = intent.extras
            val messages = bundle?.get("pdus") as Array<*>?

            messages?.forEach { message ->
                val smsMessage = SmsMessage.createFromPdu(message as ByteArray)
                val messageText = smsMessage.messageBody
                val phoneNumber = smsMessage.originatingAddress
                if (phoneNumber != null) {
                    messageCallback?.onMessageReceived(messageText, phoneNumber)
                }
            }
        }
    }
}*/


class SmsReceiver : BroadcastReceiver() {
    //working

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
