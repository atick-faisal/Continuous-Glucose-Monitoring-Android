package dev.atick.network.data

import com.google.gson.annotations.SerializedName

data class Request(
    @SerializedName("ppg_data")
    val ppgData: String,
    @SerializedName("meta_data")
    val metadata: String
)
