package com.cheerz.mediamanager.plugins

import io.ktor.server.application.Application
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.ExperimentalDatabaseMigrationApi
import org.jetbrains.exposed.sql.StdOutSqlLogger
import org.jetbrains.exposed.sql.addLogger
import org.jetbrains.exposed.sql.transactions.transaction

@OptIn(ExperimentalDatabaseMigrationApi::class)
fun Application.configureDatabase() {
    val user = environment.config.property("ktor.database.user").getString()
    val password = environment.config.property("ktor.database.password").getString()
    val host = environment.config.property("ktor.database.host").getString()
    val port = environment.config.property("ktor.database.port").getString()
    val name = environment.config.property("ktor.database.name").getString()

    val flyway = Flyway.configure()
        .dataSource("jdbc:postgresql://$host:$port/$name", user, password)
        .load()

    val database = Database.connect(
        "jdbc:postgresql://$host:$port/$name",
        driver = "org.postgresql.Driver",
        user = user,
        password = password
    )

    transaction(database) {
        addLogger(StdOutSqlLogger)
//        SchemaUtils.generateMigrationScript(
//            MediaTable,
//            scriptDirectory = "src/main/resources/db/migration",
//            scriptName = "V2__Add_Media_sha1_Unique_Index"
//        )
    }

    flyway.migrate()
}
