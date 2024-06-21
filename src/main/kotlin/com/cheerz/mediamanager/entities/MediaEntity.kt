package com.cheerz.mediamanager.entities

import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.id.EntityID
import org.jetbrains.exposed.dao.id.IntIdTable
import org.jetbrains.exposed.sql.javatime.CurrentDateTime
import org.jetbrains.exposed.sql.javatime.datetime

object MediaTable: IntIdTable("media") {
    val type = varchar("type", 255)
    val createdAt = datetime("created_at").defaultExpression(CurrentDateTime)
    val updatedAt = datetime("updated_at").defaultExpression(CurrentDateTime)
}

class MediaDAO(id: EntityID<Int>): IntEntity(id) {
    companion object: IntEntityClass<MediaDAO>(MediaTable)

    var type by MediaTable.type
    var createdAt by MediaTable.createdAt
    var updatedAt by MediaTable.updatedAt
}