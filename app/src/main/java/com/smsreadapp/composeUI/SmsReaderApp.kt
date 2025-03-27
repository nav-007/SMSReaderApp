package com.smsreadapp.composeUI

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.smsreadapp.model.SmsModel

@Composable
fun SmsReaderApp(smsList: List<SmsModel>, filterSender: String, onFilterChange: (String) -> Unit) {
    var senderId by remember { mutableStateOf(filterSender) }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        OutlinedTextField(
            value = senderId,
            onValueChange = {
                senderId = it
            },
            label = { Text("Enter Sender ID") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = androidx.compose.ui.graphics.Color.Gray, // Hint text color
                focusedLabelColor = androidx.compose.ui.graphics.Color.Blue, // Label color when focused
                unfocusedLabelColor = androidx.compose.ui.graphics.Color.DarkGray, // Label color when not focused
            )
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { onFilterChange(senderId) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Apply Filter")
        }

        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(smsList) { sms ->
                SmsItem(sms)
            }
        }
    }
}

@Composable
fun SmsItem(sms: SmsModel) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp) // Fix here
    ) {
        Column(modifier = Modifier.padding(8.dp)) {
            Text(text = "From: ${sms.sender}", style = MaterialTheme.typography.titleMedium)
            Text(text = sms.body, style = MaterialTheme.typography.bodyLarge)
            Text(text = "Dated: ${sms.getFormattedDate()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}