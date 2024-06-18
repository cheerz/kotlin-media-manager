package com.cheerz.mediamanager.models

import com.google.cloud.storage.Blob
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MediaItem(
    @SerialName("id") val id: String,
    @SerialName("type") val type: MediaType,
)

fun Blob.toMediaItem() = MediaItem(name, MediaType.IMAGE)

enum class MediaType { IMAGE, VIDEO }
