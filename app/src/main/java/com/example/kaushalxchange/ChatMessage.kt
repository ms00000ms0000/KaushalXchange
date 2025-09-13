package com.example.kaushalxchange

import org.json.JSONObject

data class ChatMessage(
    var sender: String = "",
    var message: String? = null,
    var type: String = "text", // "text" | "image" | "video" | "file"
    var fileName: String? = null,
    var fileUrl: String? = null,
    var timestamp: Long = System.currentTimeMillis(),
    var status: String = "sent" // future use: "sent", "delivered", "seen"
) {
    fun toJson(): JSONObject {
        val obj = JSONObject()
        obj.put("sender", sender)
        if (message != null) obj.put("message", message)
        obj.put("type", type)
        if (fileName != null) obj.put("fileName", fileName)
        if (fileUrl != null) obj.put("fileUrl", fileUrl)
        obj.put("timestamp", timestamp)
        obj.put("status", status)
        return obj
    }

    companion object {
        fun fromJson(obj: JSONObject): ChatMessage {
            val sender = obj.optString("sender", "")
            val message = if (obj.has("message")) obj.optString("message") else null
            val type = obj.optString("type", "text")
            val fileName = if (obj.has("fileName")) obj.optString("fileName") else null
            val fileUrl = if (obj.has("fileUrl")) obj.optString("fileUrl") else null
            val timestamp = if (obj.has("timestamp")) obj.optLong("timestamp", System.currentTimeMillis()) else System.currentTimeMillis()
            val status = obj.optString("status", "sent")
            return ChatMessage(sender, message, type, fileName, fileUrl, timestamp, status)
        }
    }
}
