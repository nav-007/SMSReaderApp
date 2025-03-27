package com.smsreadapp

import android.Manifest
import android.app.AlertDialog
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.provider.Telephony
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.smsreadapp.commonCode.SMSReceiver
import com.smsreadapp.composeUI.SmsReaderApp
import com.smsreadapp.listener.SmsListener
import com.smsreadapp.model.SmsModel
import com.smsreadapp.ui.theme.SMSReadAppTheme

class MainActivity : ComponentActivity(), SmsListener {

    private var smsReceiver: SMSReceiver? = null
    private val smsList = mutableStateListOf<SmsModel>()
    private var filterSenderId by mutableStateOf("")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        requestSmsPermissions()
        setContent {
            SMSReadAppTheme {
                // A surface container using the 'background' color from the theme
                SmsReaderApp(smsList, filterSenderId) { newFilter ->
                    filterSenderId = newFilter
                    loadSmsFromInbox()
                }

            }
        }
// Set the listener before registering receiver
        SMSReceiver.listener = this
        val intentFilter = IntentFilter(Telephony.Sms.Intents.SMS_RECEIVED_ACTION)
        registerReceiver(smsReceiver, intentFilter)

    }

    private fun requestSmsPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            requestPermissions.launch(
                arrayOf(
                    Manifest.permission.READ_SMS,
                    Manifest.permission.RECEIVE_SMS
                )
            )
        } else {
            loadSmsFromInbox()
        }
    }

//    override fun onResume() {
//        super.onResume()
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
//            == PackageManager.PERMISSION_GRANTED) {
//            loadSmsFromInbox()
//        }
//    }

    override fun onStop() {
        super.onStop()
        finish()
    }

    private val requestPermissions =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            if (permissions[Manifest.permission.READ_SMS] == true && permissions[Manifest.permission.RECEIVE_SMS] == true) {
                loadSmsFromInbox()
            } else {
                val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_SMS)
                if (!showRationale) {
                    // If "Don't ask again" is selected, show a settings dialog
                    showSettingsDialog()
                } else {
                    Toast.makeText(this, "Permission denied!", Toast.LENGTH_SHORT).show()
                }

            }
        }

    private fun showSettingsDialog() {
        AlertDialog.Builder(this)
            .setTitle("Permission Denied")
            .setMessage("You have denied SMS permission permanently. Please enable it in Settings.")
            .setPositiveButton("Go to Settings") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                    data = Uri.fromParts("package", packageName, null)
                }
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun loadSmsFromInbox() {
        smsList.clear()
        val uri: Uri = Telephony.Sms.CONTENT_URI
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_SMS)
            != PackageManager.PERMISSION_GRANTED) {
            return // Stop execution if permission is not granted
        }
        val cursor: Cursor? =
            contentResolver.query(uri, null, null, null, Telephony.Sms.DEFAULT_SORT_ORDER)

        cursor?.use {
            val indexBody = it.getColumnIndex(Telephony.Sms.BODY)
            val indexAddress = it.getColumnIndex(Telephony.Sms.ADDRESS)
            val indexDate = it.getColumnIndex(Telephony.Sms.DATE)

            while (it.moveToNext()) {
                val body = it.getString(indexBody)
                val sender = it.getString(indexAddress)
                val date = it.getLong(indexDate)

                if (filterSenderId.isEmpty() || sender.contains(filterSenderId, true)) {
                    smsList.add(SmsModel(sender, body, date))
                }
            }
        }
    }

    override fun onSmsReceived(sms: SmsModel) {
        if (filterSenderId.isEmpty() || sms.sender.contains(filterSenderId, true)) {
            smsList.add(0, sms)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        smsReceiver?.let { unregisterReceiver(it) }
    }

}