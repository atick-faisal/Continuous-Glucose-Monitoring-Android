package dev.atick.network.repository

import dev.atick.network.data.Request
import dev.atick.network.data.Response

interface GlucoseRepository {
    suspend fun getGlucosePrediction(request: Request): Response?
}