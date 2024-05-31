package com.cheerz.mediamanager.plugins

import com.cheerz.mediamanager.routes.download
import com.cheerz.mediamanager.routes.getMediaRoute
import com.cheerz.mediamanager.routes.homeRoute
import com.cheerz.mediamanager.routes.listMediaRoute
import com.cheerz.mediamanager.routes.testSerializationRoute
import com.cheerz.mediamanager.routes.upload
import io.ktor.server.application.Application
import io.ktor.server.routing.routing

fun Application.configureRouting() {
    routing {
        homeRoute()

        listMediaRoute()
        getMediaRoute()
        download()
        upload()

        testSerializationRoute()
    }
}
