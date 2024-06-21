package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.entities.MediaDAO
import com.cheerz.mediamanager.models.CheerzResponse
import com.cheerz.mediamanager.models.MediaItem
import com.cheerz.mediamanager.models.MediaType
import com.cheerz.mediamanager.models.toMediaItem
import com.cheerz.mediamanager.storage
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
import org.jetbrains.exposed.sql.transactions.transaction
import javax.print.attribute.standard.Media


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
                    val uuid = java.util.UUID.randomUUID().toString()
                    val fileBytes = part.streamProvider().readBytes()

                    getBucket().create(uuid, fileBytes, part.contentType.toString())

                    val media = MediaItem(uuid, MediaType.IMAGE)

                    transaction {
                        MediaDAO.new {
                            type = media.type.toString()
                        }
                    }

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

        val blob = getBucket().get(id)

        if (blob == null) {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(
                CheerzResponse(null, "Not found")
            )
            return@get
        }

        call.respondBytes(blob.getContent(), ContentType.Image.JPEG, HttpStatusCode.OK)
    }
}

private fun PipelineContext<*, ApplicationCall>.getBucket(): Bucket {
    val bucketName = call.application.environment.config.property("ktor.storage.bucket_name").getString()
    return storage.get(bucketName)
}
