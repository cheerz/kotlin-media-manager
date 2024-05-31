package com.cheerz.mediamanager.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CheerzResponse<Payload>(
    @SerialName("response") val response: Payload,
    @SerialName("error_message") val errorMessage: String?,
)
