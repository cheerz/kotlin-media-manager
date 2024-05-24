package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.storage.mediaStorage
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.listMediaRoute() {
    get("/media") {
        if (mediaStorage.isNotEmpty()) {
            call.respond(mediaStorage)
        }
    }
}

fun Route.getMediaRoute() {
    get("/media/{id?}") {
        val id = call.parameters["id"] ?: return@get call.respondText("Bad Request", status = HttpStatusCode.BadRequest)
        val media = mediaStorage.find { it.id == id } ?: return@get call.respondText(
            "Not Found",
            status = HttpStatusCode.NotFound
        )
        call.respond(media)
    }
}