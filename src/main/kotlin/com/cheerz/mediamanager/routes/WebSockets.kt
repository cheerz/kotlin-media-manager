package com.cheerz.mediamanager.routes

import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import io.ktor.serialization.deserialize

@Serializable
data class Message(val text: String)

@Serializable
data class MessageResponse(val text: String, val code: Int)

fun Route.websocketRoutes() = route("/websocket") {
    webSocket("/echo") {
        val converter = this.converter
        this.send(Frame.Text("Please enter your name"))
        
        for (frame in incoming) {
            val message = converter?.deserialize<Message>(frame)
            
            message?.let {
                sendSerialized(MessageResponse("Bien note ${message.text}", 200))
            }
        }
    }   
}