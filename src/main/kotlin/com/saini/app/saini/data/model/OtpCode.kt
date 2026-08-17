package com.saini.app.saini.data.model

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "otp_codes")
data class OtpCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,
    
    val mobileNo: String = "",
    val code: String = "",
    val expiryTime: LocalDateTime = LocalDateTime.now().plusMinutes(5)
)
