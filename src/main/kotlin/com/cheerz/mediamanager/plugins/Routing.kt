package com.cheerz.mediamanager.plugins

import com.cheerz.mediamanager.routes.*
import io.ktor.server.application.*
import io.ktor.server.routing.*

fun Application.configureRouting() {
    routing {
        homeRoute()

        testSerializationRoute()
    }
}
