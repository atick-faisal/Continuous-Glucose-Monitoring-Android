package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class Response(
    @SerializedName("glocose_predict")
    val glucosePredict: String?
)
