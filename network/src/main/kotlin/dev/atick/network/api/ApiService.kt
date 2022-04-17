package dev.atick.network.api

import dev.atick.network.data.Request
import dev.atick.network.data.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("/projects/glucose-predictor")
    suspend fun sendData(@Body request: Request): Response?
}