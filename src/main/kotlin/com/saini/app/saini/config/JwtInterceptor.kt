package com.saini.app.saini.config

import com.saini.app.saini.util.JwtUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class JwtInterceptor(private val jwtUtil: JwtUtil) : HandlerInterceptor {

    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        // Option to skip auth for certain methods (like OPTIONS for CORS)
        if (request.method.equals("OPTIONS", ignoreCase = true)) {
            return true
        }

        val authHeader = request.getHeader("Authorization")

        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Missing or invalid Authorization header")
            return false
        }

        val token = authHeader.substring(7)
        return if (jwtUtil.isTokenValid(token)) {
            // Optional: Store mobileNo or userId in request for controller use
            val mobileNo = jwtUtil.extractMobileNo(token)
            request.setAttribute("userMobile", mobileNo)
            true
        } else {
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid or expired token")
            false
        }
    }
}
