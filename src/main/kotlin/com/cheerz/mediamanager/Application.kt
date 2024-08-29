package com.cheerz.mediamanager

import com.cheerz.mediamanager.plugins.configureDatabase
import com.cheerz.mediamanager.plugins.configureRouting
import com.cheerz.mediamanager.plugins.configureSerialization
import com.google.cloud.storage.StorageOptions
import io.ktor.serialization.kotlinx.*
import io.ktor.server.application.*
import io.ktor.server.netty.EngineMain
import io.ktor.server.websocket.*
import kotlinx.serialization.json.Json

val storage = StorageOptions.getDefaultInstance().service

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    install(WebSockets) {
        contentConverter = KotlinxWebsocketSerializationConverter(Json)
    }

    configureSerialization()
    configureRouting()
    configureDatabase()
}
