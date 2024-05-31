package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.models.CheerzResponse
import com.cheerz.mediamanager.models.MediaItem
import com.cheerz.mediamanager.models.MediaType
import com.cheerz.mediamanager.models.toMediaItem
import com.cheerz.mediamanager.storage
import com.google.cloud.storage.Storage
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.response.respondFile
import io.ktor.server.routing.*
import java.io.File


fun Route.mediaRoutes() = route("/media") {
    upload()
    download()
    getAll()
}

private fun Route.getAll() {
    get {
        val bucketName = call.application.environment.config.property("ktor.storage.bucket_name").getString()
        val buckets = storage.get(bucketName).list(Storage.BlobListOption.currentDirectory()).values
        val medias = buckets
            .filter { it.contentType?.startsWith("image") == true }
            .map { it.toMediaItem() }

        call.response.status(HttpStatusCode.OK)
        call.respond(
            CheerzResponse(medias, null)
        )
    }
}

private fun Route.upload() {
    post("/upload") {
        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val uuid = java.util.UUID.randomUUID().toString()
                    val fileBytes = part.streamProvider().readBytes()

                    val bucketName = call.application.environment.config.property("ktor.storage.bucket_name").getString()
                    storage.get(bucketName).create(uuid, fileBytes, part.contentType.toString())

                    val media = MediaItem(uuid, MediaType.IMAGE)
                    call.response.status(HttpStatusCode.Created)
                    call.respond(
                        CheerzResponse(media, null)
                    )
                }
                else -> {
                    call.response.status(HttpStatusCode.BadRequest)
                    call.respond(
                        CheerzResponse(null, "Bad request")
                    )
                }
            }
            part.dispose()
        }

        call.response.status(HttpStatusCode.BadRequest)
        call.respond(
            CheerzResponse(null, "Bad request")
        )
    }
}

private fun Route.download() {
    get("/download/{id}") {
        val id = call.parameters["id"]

        val bucketName = call.application.environment.config.property("ktor.storage.bucket_name").getString()
        val blob = storage.get(bucketName).get(id)

        call.respondBytes(blob.getContent(), ContentType.Image.JPEG, HttpStatusCode.OK)
    }
}
