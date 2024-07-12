package com.cheerz.mediamanager.models

import com.google.cloud.storage.Blob
import io.ktor.http.*
import io.ktor.http.content.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import java.security.MessageDigest

@Serializable
data class MediaItem(
    @SerialName("sha1") val sha1: String,
    @SerialName("type") val type: MediaType,
)

fun PartData.FileItem.toMediaItem(fileBytes: ByteArray): MediaItem {
    val mediaSha1 = sha1(fileBytes)
    return MediaItem(mediaSha1, contentType.toMediaType())
}

fun Blob.toMediaItem() = MediaItem(name, toMediaType())

fun Blob.toMediaType(): MediaType = contentType.toMediaType()

fun ContentType?.toMediaType(): MediaType = this?.contentType?.toMediaType() ?: MediaType.UNKNOWN

private fun String.toMediaType(): MediaType = when {
    startsWith("video") -> MediaType.VIDEO
    startsWith("image") -> MediaType.IMAGE
    else -> MediaType.UNKNOWN
}

private fun sha1(input: ByteArray): String {
    val md = MessageDigest.getInstance("SHA-1")
    val digest = md.digest(input)
    return digest.joinToString("") { "%02x".format(it) }
}

enum class MediaType { IMAGE, VIDEO, UNKNOWN }
