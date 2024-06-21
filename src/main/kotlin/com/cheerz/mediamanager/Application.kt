package com.cheerz.mediamanager

import com.cheerz.mediamanager.plugins.configureDatabase
import com.cheerz.mediamanager.plugins.configureRouting
import com.cheerz.mediamanager.plugins.configureSerialization
import com.google.cloud.storage.StorageOptions
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain

val storage = StorageOptions.getDefaultInstance().service

fun main(args: Array<String>): Unit = EngineMain.main(args)

fun Application.module() {
    configureSerialization()
    configureRouting()
    configureDatabase()
}
