package com.saini.app.saini.data.model

import jakarta.persistence.*

@Entity
@Table(name = "users")
data class User(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Assigning default empty strings "" creates a hidden no-arg constructor for Hibernate
    var fullName: String = "",
    var email: String = "",
    var mobileNo: String = "",
    val password: String = "",
    val gender: String = "",
    @Column(name = "role", nullable = false)
    var role: String = "USER", // Default for everyone

    var showContact: Boolean = false,
    var showProfile: Boolean = true,
    
    var isSubscribed: Boolean = false,
    var subscriptionExpiry: java.time.LocalDateTime? = null
)
