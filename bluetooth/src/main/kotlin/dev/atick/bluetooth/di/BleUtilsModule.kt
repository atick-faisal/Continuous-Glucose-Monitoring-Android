package dev.atick.bluetooth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.bluetooth.utils.BtUtils
import dev.atick.bluetooth.utils.BtUtilsImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class BleUtilsModule {
    @Binds
    @Singleton
    abstract fun bindBleUtils(
        bleUtilsImpl: BtUtilsImpl
    ): BtUtils
}