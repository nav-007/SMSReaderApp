package com.smsreadapp.model

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class SmsModel(
    val sender: String,
    val body: String,
    val timestamp: Long
){
    fun getFormattedDate(): String {
        val date = Date(timestamp)
        val format = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())
        return format.format(date)
    }
}