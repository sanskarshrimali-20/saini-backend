package com.saini.app.saini.data.response

data class AuthResponse(
    val id: Long?,
    val fullName: String,
    val email: String,
    val mobileNo: String,
    val gender: String,
    val token: String,
    val role: String,
    val isSubscribed: Boolean
)
