package com.cheerz.mediamanager.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object MediaTable: IntIdTable("media") {
    val sha1 = varchar("sha1", 255)
    val type = varchar("type", 255)
    val lastAccessedAt = datetime("last_accessed_at").nullable()
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

class MediaDAO(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<MediaDAO>(MediaTable)

    var sha1 by MediaTable.sha1
    var type by MediaTable.type
    var lastAccessedAt by MediaTable.lastAccessedAt
    var createdAt by MediaTable.createdAt
    var updatedAt by MediaTable.updatedAt
}