package com.saini.app.saini.controller

import com.saini.app.saini.data.model.User
import com.saini.app.saini.data.model.OtpCode
import com.saini.app.saini.data.response.AuthResponse
import com.saini.app.saini.repository.UserRepository
import com.saini.app.saini.repository.OtpRepository
import com.saini.app.saini.util.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime
import java.util.*
import org.springframework.transaction.annotation.Transactional

@RestController
@RequestMapping("/api/saini")
class AuthController(
    private val userRepository: UserRepository,
    private val otpRepository: OtpRepository,
    private val jwtUtil: JwtUtil
) {

    @PostMapping("/auth/login")
    fun login(@RequestBody loginRequest: User): ResponseEntity<Any> {
        val user = userRepository.findByMobileNo(loginRequest.mobileNo)

        if (user != null && user.password == loginRequest.password) {
            val dynamicToken = jwtUtil.generateToken(user.mobileNo)

            val authResponse = AuthResponse(
                id = user.id,
                fullName = user.fullName,
                email = user.email,
                mobileNo = user.mobileNo,
                token = dynamicToken,
                gender = user.gender,
                role = user.role,
                isSubscribed = user.isSubscribed
            )
            
            return ResponseEntity.ok(authResponse)
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid mobile number or password")
    }

    @PostMapping("/auth/signup")
    fun signup(@RequestBody signupRequest: User): ResponseEntity<Any> {
        if (userRepository.findByMobileNo(signupRequest.mobileNo) != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Mobile Number is already taken!")
        }

        val newUser = User(
            fullName = signupRequest.fullName,
            email = signupRequest.email,
            mobileNo = signupRequest.mobileNo,
            gender = signupRequest.gender,
            password = signupRequest.password
        )

        val savedUser = userRepository.save(newUser)
        val dynamicToken = jwtUtil.generateToken(savedUser.mobileNo)

        val authResponse = AuthResponse(
            id = savedUser.id,
            fullName = savedUser.fullName,
            email = savedUser.email,
            mobileNo = savedUser.mobileNo,
            token = dynamicToken,
            gender = savedUser.gender,
            role = savedUser.role,
            isSubscribed = savedUser.isSubscribed
        )

        return ResponseEntity.status(HttpStatus.CREATED).body(authResponse)
    }

    @PostMapping("/auth/forgot-password")
    @Transactional
    fun forgotPassword(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val mobileNo = body["mobileNo"] ?: return ResponseEntity.badRequest().body("Mobile number is required")
        
        val user = userRepository.findByMobileNo(mobileNo)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found with this mobile number")

        val otp = (100000..999999).random().toString()
        
        otpRepository.deleteByMobileNo(mobileNo)

        val otpCode = OtpCode(
            mobileNo = mobileNo,
            code = otp,
            expiryTime = LocalDateTime.now().plusMinutes(5)
        )
        otpRepository.save(otpCode)

        println("Sending OTP $otp to $mobileNo")

        return ResponseEntity.ok(mapOf("success" to true, "message" to "OTP sent successfully", "otp" to otp))
    }

    @PostMapping("/auth/reset-password")
    @Transactional
    fun resetPassword(@RequestBody body: Map<String, String>): ResponseEntity<Any> {
        val mobileNo = body["mobileNo"] ?: return ResponseEntity.badRequest().body("Mobile number is required")
        val otp = body["otp"] ?: return ResponseEntity.badRequest().body("OTP is required")
        val newPassword = body["newPassword"] ?: return ResponseEntity.badRequest().body("New password is required")

        val otpRecord = otpRepository.findTopByMobileNoOrderByExpiryTimeDesc(mobileNo)

        if (otpRecord == null || otpRecord.code != otp || otpRecord.expiryTime.isBefore(LocalDateTime.now())) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Invalid or expired OTP")
        }

        val user = userRepository.findByMobileNo(mobileNo)
            ?: return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found")

        val updatedUser = user.copy(password = newPassword)
        userRepository.save(updatedUser)
        
        otpRepository.deleteByMobileNo(mobileNo)

        return ResponseEntity.ok(mapOf("success" to true, "message" to "Password reset successfully"))
    }

    @PostMapping("/auth/update-profile")
    fun updateProfile(@RequestBody body: Map<String, String>, request: HttpServletRequest): ResponseEntity<Any> {
        val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
        val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()

        val updatedUser = user.copy(
            fullName = body["fullName"] ?: user.fullName,
            email = body["email"] ?: user.email
        )
        userRepository.save(updatedUser)
        return ResponseEntity.ok(mapOf("success" to true, "message" to "Profile updated", "user" to updatedUser))
    }

    @PostMapping("/auth/change-password")
    fun changePassword(@RequestBody body: Map<String, String>, request: HttpServletRequest): ResponseEntity<Any> {
        val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
        val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()

        val oldPassword = body["oldPassword"] ?: return ResponseEntity.badRequest().body("Old password required")
        val newPassword = body["newPassword"] ?: return ResponseEntity.badRequest().body("New password required")

        if (user.password != oldPassword) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Incorrect old password")
        }

        val updatedUser = user.copy(password = newPassword)
        userRepository.save(updatedUser)
        return ResponseEntity.ok(mapOf("success" to true, "message" to "Password changed successfully"))
    }

    @PostMapping("/auth/privacy-settings")
    fun updatePrivacy(@RequestBody body: Map<String, Boolean>, request: HttpServletRequest): ResponseEntity<Any> {
        val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
        val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()

        val updatedUser = user.copy(
            showContact = body["showContact"] ?: user.showContact,
            showProfile = body["showProfile"] ?: user.showProfile
        )
        userRepository.save(updatedUser)
        return ResponseEntity.ok(mapOf("success" to true, "message" to "Privacy settings updated"))
    }

    @GetMapping("/auth/me")
    fun getCurrentUser(request: HttpServletRequest): ResponseEntity<Any> {
        val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
        val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()
        return ResponseEntity.ok(user)
    }
}
