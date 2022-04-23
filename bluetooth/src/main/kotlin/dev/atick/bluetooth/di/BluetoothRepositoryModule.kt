package dev.atick.bluetooth.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.bluetooth.repository.BtManager
import dev.atick.bluetooth.repository.BtManagerImpl
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class BluetoothRepositoryModule {
    @Binds
    @Singleton
    abstract fun bindBluetoothRepository(
        bluetoothRepositoryImpl: BtManagerImpl
    ): BtManager
}