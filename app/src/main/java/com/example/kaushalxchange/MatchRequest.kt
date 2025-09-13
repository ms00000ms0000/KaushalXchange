package com.example.kaushalxchange

data class MatchRequest(
    val fromUid: String = "",
    val fromName: String = "",
    val skills_want_to_learn: List<String> = emptyList(),
    val skills_can_teach: List<String> = emptyList(), // what requester can teach / offers
    val status: String = "pending", // pending / accepted / rejected
    val timestamp: Long = 0
)
