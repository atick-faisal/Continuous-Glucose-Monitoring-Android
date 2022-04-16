package dev.atick.bluetooth.utils

import androidx.activity.ComponentActivity

interface BtUtils {

    fun initialize(activity: ComponentActivity, onSuccess: () -> Unit)
    fun isAllPermissionsProvided(activity: ComponentActivity): Boolean
    fun setupBluetooth(activity: ComponentActivity)

}