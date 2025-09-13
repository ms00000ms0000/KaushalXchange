package com.example.kaushalxchange

data class User(
    val name: String,
    val profileImage: Int,
    var unreadCount: Int = 0,
    val uid: String = ""
)
