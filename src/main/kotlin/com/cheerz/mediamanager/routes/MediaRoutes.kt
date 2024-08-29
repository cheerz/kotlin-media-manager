package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.entities.MediaDAO
import com.cheerz.mediamanager.entities.MediaTable
import com.cheerz.mediamanager.models.*
import com.cheerz.mediamanager.storage
import com.cheerz.mediamanager.uploadStatusFlow
import com.google.cloud.storage.Bucket
import com.google.cloud.storage.Storage
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondBytes
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.util.pipeline.PipelineContext
import kotlinx.coroutines.launch
import org.jetbrains.exposed.sql.transactions.transaction
import java.time.LocalDateTime


fun Route.mediaRoutes() = route("/media") {
    listMedias(contentType = "image")
    listMedias(contentType = "video")
    upload()
    download()
}

private fun Route.listMedias(contentType: String) {
    get("/${contentType}s") {
        val buckets = getBucket().list(Storage.BlobListOption.currentDirectory()).values
        val medias = buckets
            .filter { it.contentType?.startsWith(contentType) == true }
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
                    val fileBytes = part.streamProvider().readBytes()
                    val media = part.toMediaItem(fileBytes)
                    val contentType = part.contentType

                    val hasExistingMedia = transaction {
                        val existingMedia = MediaDAO.find { MediaTable.sha1 eq media.sha1 }
                        return@transaction !existingMedia.empty()
                    }

                    if (hasExistingMedia) {
                        call.response.status(HttpStatusCode.Conflict)
                        call.respond(
                            CheerzResponse(null, "Conflict")
                        )
                        return@forEachPart
                    }

                    getBucket().create(media.sha1, fileBytes, contentType.toString())

                    transaction {
                        MediaDAO.new {
                            sha1 = media.sha1
                            type = media.type.toString()
                        }
                    }

                    call.response.status(HttpStatusCode.Created)

                    // Send success upload notif to websocket
                    uploadStatusFlow.value = "${media.sha1} uploaded"

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
    get("/download/{sha1}") {
        val requestedSha1 = requireNotNull(call.parameters["sha1"])

        val blob = getBucket().get(requestedSha1)

        if (blob == null) {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(
                CheerzResponse(null, "Not found")
            )
            return@get
        }

        transaction {
            val media = MediaDAO.find { MediaTable.sha1 eq requestedSha1 }.singleOrNull()
            media?.lastAccessedAt = LocalDateTime.now()
        }

        call.respondBytes(blob.getContent(), ContentType.parse(blob.contentType), HttpStatusCode.OK)
    }
}

private fun PipelineContext<*, ApplicationCall>.getBucket(): Bucket {
    val bucketName = call.application.environment.config.property("ktor.storage.bucket_name").getString()
    return storage.get(bucketName)
}
