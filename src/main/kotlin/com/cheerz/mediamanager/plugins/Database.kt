package com.cheerz.mediamanager.plugins

import io.ktor.server.application.*
import org.jetbrains.exposed.sql.Database

fun Application.configureDatabase() {
    val user = environment.config.property("ktor.database.user").getString()
    val password = environment.config.property("ktor.database.password").getString()
    val host = environment.config.property("ktor.database.host").getString()
    val port = environment.config.property("ktor.database.port").getString()
    val name = environment.config.property("ktor.database.name").getString()

    Database.connect(
        "\"jdbc:postgresql://$host:$port/$name\",",
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )
}