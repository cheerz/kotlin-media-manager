package com.cheerz.mediamanager

import com.cheerz.mediamanager.plugins.configureRouting
import com.cheerz.mediamanager.plugins.configureSerialization
import com.google.cloud.storage.StorageOptions
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

val storage = StorageOptions.getDefaultInstance().service

fun main() {
    embeddedServer(Netty, port = 8080, host = "0.0.0.0", module = Application::module)
        .start(wait = true)
}

fun Application.module() {
    configureSerialization()
    configureRouting()
}
