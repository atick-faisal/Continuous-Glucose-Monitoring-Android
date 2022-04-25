package dev.atick.sms.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.sms.utils.SmsUtils
import dev.atick.sms.utils.SmsUtilsImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SmsUtilsModule {
    @Binds
    @Singleton
    abstract fun bindSmsUtils(
        smsUtilsImpl: SmsUtilsImpl
    ): SmsUtils
}