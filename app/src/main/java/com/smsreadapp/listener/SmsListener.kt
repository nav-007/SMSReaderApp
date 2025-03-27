package com.smsreadapp.listener

import com.smsreadapp.model.SmsModel

interface SmsListener {
    fun onSmsReceived(sms: SmsModel)
}