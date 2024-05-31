package com.cheerz.mediamanager.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    @SerialName("id") val id: String,
    @SerialName("type") val type: MediaType,
)

enum class MediaType { IMAGE, VIDEO }
