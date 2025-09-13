package com.example.kaushalxchange


data class UserProfile(
    var uid: String? = null,
    var displayName: String? = null,
    var bio: String? = null,
    var skills_can_teach: List<String> = emptyList(),
    var skills_want_to_learn: List<String> = emptyList()
)