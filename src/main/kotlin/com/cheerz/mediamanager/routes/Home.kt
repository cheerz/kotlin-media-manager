package com.cheerz.mediamanager.routes

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.homeRoute() {
    get("/") {
        call.respondText("Hello World!")
    }
}