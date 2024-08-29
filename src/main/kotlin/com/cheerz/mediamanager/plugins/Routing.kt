package com.cheerz.mediamanager.plugins

import com.cheerz.mediamanager.routes.*
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        homeRoute()
        mediaRoutes()
        websocketRoutes()
    }
}
