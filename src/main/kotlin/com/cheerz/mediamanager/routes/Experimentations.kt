package com.cheerz.mediamanager.routes

import io.ktor.server.application.call
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.testSerializationRoute() {
    get("/json/kotlinx-serialization") {
        call.respond(mapOf("hello" to "world"))
    }
}
