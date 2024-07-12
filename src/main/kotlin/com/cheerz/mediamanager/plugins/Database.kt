package com.cheerz.mediamanager.plugins

import com.cheerz.mediamanager.entities.MediaTable
import io.ktor.server.application.Application
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

fun Application.configureDatabase() {
    val user = environment.config.property("ktor.database.user").getString()
    val password = environment.config.property("ktor.database.password").getString()
    val host = environment.config.property("ktor.database.host").getString()
    val port = environment.config.property("ktor.database.port").getString()
    val name = environment.config.property("ktor.database.name").getString()

    val database = Database.connect(
        "jdbc:postgresql://$host:$port/$name",
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    transaction(database) {
        addLogger(StdOutSqlLogger)
        SchemaUtils.create(MediaTable)
    }
}
