package dev.atick.network.repository

import dev.atick.network.api.ApiService
import dev.atick.network.data.Request
import dev.atick.network.data.Response
import javax.inject.Inject

class GlucoseRepositoryImpl @Inject constructor(
    private val apiService: ApiService
) : GlucoseRepository {
    override suspend fun getGlucosePrediction(request: Request): Response? {
        return apiService.sendData(request)
    }
}