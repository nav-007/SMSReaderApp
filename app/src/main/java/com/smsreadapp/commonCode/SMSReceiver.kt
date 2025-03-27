package com.smsreadapp.commonCode

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.provider.Telephony
import com.smsreadapp.listener.SmsListener
import com.smsreadapp.model.SmsModel

class SMSReceiver: BroadcastReceiver() {

    companion object {
        var listener: SmsListener? = null
    }
    override fun onReceive(context: Context, intent: Intent) {
        if (Telephony.Sms.Intents.SMS_RECEIVED_ACTION == intent.action) {
            for (smsMessage in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                val sender = smsMessage.displayOriginatingAddress
                val body = smsMessage.messageBody
                val timestamp = smsMessage.timestampMillis

                listener?.onSmsReceived(SmsModel(sender, body, timestamp))
            }
        }
    }

}