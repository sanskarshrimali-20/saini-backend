package com.saini.app.saini.util

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.stereotype.Component
import java.util.Date
import javax.crypto.SecretKey

@Component
class JwtUtil {
    // Standard secure 256-bit key for HMAC-SHA signature authentication
    private val secretString = "your-super-secret-secure-key-that-must-be-at-least-256-bits-long!!"
    private val key: SecretKey = Keys.hmacShaKeyFor(secretString.toByteArray())
    
    // Token validity period window (e.g., 24 hours in milliseconds)
    private val expirationPeriodMs = 86400000 

    fun generateToken(mobileNo: String): String {
        val now = Date()
        val expiryDate = Date(now.time + expirationPeriodMs)

        return Jwts.builder()
            .subject(mobileNo)
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(key)
            .compact()
    }

    // 1. Extract the mobile number (Subject) from the token string
    fun extractMobileNo(token: String): String {
        // Use explicit .getSubject() function to avoid map key conflicts in Kotlin
        return extractAllClaims(token).subject
    }

    // 2. Extract all information packed inside the token
    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key) // Verifies the signature mathematically
            .build()
            .parseSignedClaims(token) // Parses the wrapper
            .payload // Extracts the actual Claims body map
    }

    // 3. Check if the token has expired yet
    fun isTokenValid(token: String): Boolean {
        return try {
            // Use explicit .expiration to grab the Date object safely
            val expiration = extractAllClaims(token).expiration
            !expiration.before(Date())
        } catch (e: Exception) {
            false // If parsing fails or token is altered/expired, it's invalid
        }
    }
}