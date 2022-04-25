package dev.atick.sms.utils

import androidx.activity.ComponentActivity

interface SmsUtils {
    fun initialize(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    )
    fun askForSmsPermission()
    fun sendSms(
        phone: String,
        message: String
    )
}