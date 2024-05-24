package com.cheerz.mediamanager.models

data class MediaItem(
    val id: String,
    val type: MediaType
)

enum class MediaType { IMAGE, VIDEO }