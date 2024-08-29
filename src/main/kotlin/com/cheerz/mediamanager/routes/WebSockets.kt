package com.cheerz.mediamanager.routes

import com.cheerz.mediamanager.uploadStatusFlow
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import kotlinx.serialization.Serializable
import io.ktor.serialization.deserialize
import kotlinx.coroutines.launch

@Serializable
data class Message(val text: String)

@Serializable
data class MessageResponse(val text: String, val code: Int)

fun Route.websocketRoutes() = route("/websocket") {
    webSocket("/echo") {
        val converter = this.converter
        this.send(Frame.Text("Please enter your name"))

        launch {
            uploadStatusFlow.collect {
                sendSerialized(MessageResponse(it, 200))
            }
        }

        for (frame in incoming) {
            val message = converter?.deserialize<Message>(frame)
            
            message?.let {
                sendSerialized(MessageResponse("Bien noté ${message.text}", 200))
            }
        }
    }   
}