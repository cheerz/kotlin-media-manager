package com.cheerz.mediamanager.routes

import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.homeRoute() {
    get("/") {
        call.respondText("Hello World!")
    }
}
