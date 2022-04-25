package dev.atick.sms.di

import android.content.Context
import android.os.Build
import android.telephony.SmsManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@Suppress("DEPRECATION")
@InstallIn(SingletonComponent::class)
object SmsManagerModule {

    @Provides
    @Singleton
    fun provideSmsManager(
        @ApplicationContext appContext: Context
    ): SmsManager? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
            appContext.getSystemService(SmsManager::class.java)
        else SmsManager.getDefault()
    }
}