package com.saini.app.saini.controller

import com.saini.app.saini.service.SubscriptionService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/saini/subscription")
class SubscriptionController(private val subscriptionService: SubscriptionService) {

    @PostMapping("/create-order")
    fun createOrder(@RequestBody body: Map<String, Int>): ResponseEntity<Any> {
        return try {
            val amount = body["amount"] ?: return ResponseEntity.badRequest().body("Amount is required")
            val orderId = subscriptionService.createOrder(amount)
            ResponseEntity.ok(mapOf("success" to true, "orderId" to orderId))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }

    @PostMapping("/verify-payment")
    fun verifyPayment(@RequestBody body: Map<String, String>, request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val orderId = body["razorpay_order_id"] ?: return ResponseEntity.badRequest().body("Order ID required")
            val paymentId = body["razorpay_payment_id"] ?: return ResponseEntity.badRequest().body("Payment ID required")
            val signature = body["razorpay_signature"] ?: return ResponseEntity.badRequest().body("Signature required")

            val success = subscriptionService.verifyPaymentAndSubscribe(userMobile, orderId, paymentId, signature)
            if (success) {
                ResponseEntity.ok(mapOf("success" to true, "message" to "Subscription active"))
            } else {
                ResponseEntity.badRequest().body(mapOf("success" to false, "message" to "Payment verification failed"))
            }
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }
}
