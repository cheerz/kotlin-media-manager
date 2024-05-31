package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.storage
import io.ktor.server.application.call
import io.ktor.server.response.respondText
import io.ktor.server.routing.Route
import io.ktor.server.routing.get

fun Route.homeRoute() {
    get("/") {
        val buckets = storage.get("media_uploader").list().values
        for (bucket in buckets) {
            println(bucket.name)
        }

        call.respondText("Hello World!")
    }
}
