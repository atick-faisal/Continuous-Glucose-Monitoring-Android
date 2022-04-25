package dev.atick.sms.utils

import android.Manifest
import android.telephony.SmsManager
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import com.orhanobut.logger.Logger
import dev.atick.core.utils.extensions.permissionLauncher
import javax.inject.Inject

class SmsUtilsImpl @Inject constructor(
    private val smsManager: SmsManager?
) : SmsUtils {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>

    override fun initialize(
        activity: ComponentActivity,
        onSuccess: () -> Unit
    ) {
        permissionLauncher = activity.permissionLauncher(
            onSuccess = onSuccess,
            onFailure = { activity.finishAffinity() }
        )
    }

    override fun askForSmsPermission() {
        Logger.i("ASKING FOR SMS PERMISSION")
        val permissions = mutableListOf(Manifest.permission.SEND_SMS)
        permissionLauncher.launch(permissions.toTypedArray())
    }

    override fun sendSms(phone: String, message: String) {
        if (smsManager == null) {
            Logger.e("CAN'T SEND SMS")
            return
        }
        smsManager.sendTextMessage(
            phone, null, message, null, null
        )
        Logger.i("SMS SENT")
    }
}