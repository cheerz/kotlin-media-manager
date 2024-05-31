package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.models.CheerzResponse
import com.cheerz.mediamanager.models.MediaItem
import com.cheerz.mediamanager.models.MediaType
import com.cheerz.mediamanager.storage.mediaStorage
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.content.PartData
import io.ktor.http.content.forEachPart
import io.ktor.http.content.streamProvider
import io.ktor.server.application.call
import io.ktor.server.request.receiveMultipart
import io.ktor.server.response.respond
import io.ktor.server.response.respondFile
import io.ktor.server.response.respondText
import io.ktor.server.routing.*
import java.io.File


fun Route.mediaRoutes() = route("/media") {
    upload()
    download()

    get("/") {
        if (mediaStorage.isNotEmpty()) {
            call.respond(mediaStorage)
        }
    }

    get("/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText(
            "Bad Request",
            status = HttpStatusCode.BadRequest
        )
        val media = mediaStorage.find { it.id == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(media)
    }
}

private fun Route.upload() {
    post("/upload") {
        val multipart = call.receiveMultipart()
        multipart.forEachPart { part ->
            when (part) {
                is PartData.FileItem -> {
                    val directory = File("medias")
                    if (!directory.exists()) {
                        directory.mkdirs()
                    }

                    val uuid = java.util.UUID.randomUUID().toString()
                    val fileBytes = part.streamProvider().readBytes()
                    File("medias/$uuid")
                        .writeBytes(fileBytes)

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

        val file = File("medias/$id")
        if (!file.exists()) {
            call.response.status(HttpStatusCode.NotFound)
            call.respond(
                CheerzResponse(null, "Not found")
            )
            return@get
        }

        call.response.headers.append(HttpHeaders.ContentType, ContentType.Image.JPEG.toString())
        call.respondFile(file)
    }
}
