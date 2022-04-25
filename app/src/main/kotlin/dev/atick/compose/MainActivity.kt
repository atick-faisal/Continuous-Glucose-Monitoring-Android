package dev.atick.compose

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.orhanobut.logger.Logger
import dagger.hilt.android.AndroidEntryPoint
import dev.atick.bluetooth.utils.BtUtils
import dev.atick.sms.utils.SmsUtils
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    @Inject
    lateinit var smsUtils: SmsUtils

    @Inject
    lateinit var btUtils: BtUtils

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btUtils.initialize(this) {
            Logger.i("BLUETOOTH SETUP SUCCESSFUL")
            smsUtils.askForSmsPermission()
        }

        smsUtils.initialize(this) {
            Logger.i("SMS PERMISSION GRANTED")
        }

    }

    override fun onResume() {
        super.onResume()
        btUtils.setupBluetooth(this)
    }
}