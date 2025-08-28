package com.example.kaushalxchange

data class Question(
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    var isAnswered: Boolean = false
)
