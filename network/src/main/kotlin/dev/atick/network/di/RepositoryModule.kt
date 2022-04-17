package dev.atick.network.di

import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import dev.atick.network.repository.GlucoseRepository
import dev.atick.network.repository.GlucoseRepositoryImpl
import javax.inject.Singleton


@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindGlucoseRepository(
        glucoseRepositoryImpl: GlucoseRepositoryImpl
    ): GlucoseRepository
}