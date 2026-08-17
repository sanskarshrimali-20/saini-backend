package com.saini.app.saini.service

import com.razorpay.Order
import com.razorpay.RazorpayClient
import com.razorpay.Utils
import com.saini.app.saini.repository.UserRepository
import org.json.JSONObject
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class SubscriptionService(
    private val userRepository: UserRepository,
    @Value("\${razorpay.key.id}") private val apiKey: String,
    @Value("\${razorpay.key.secret}") private val apiSecret: String
) {

    fun createOrder(amount: Int): String {
        val client = RazorpayClient(apiKey, apiSecret)
        val orderRequest = JSONObject()
        orderRequest.put("amount", amount * 100) // amount in the smallest currency unit
        orderRequest.put("currency", "INR")
        orderRequest.put("receipt", "order_rcptid_${System.currentTimeMillis()}")
        
        val order: Order = client.orders.create(orderRequest)
        return order.get("id")
    }

    fun verifyPaymentAndSubscribe(
        userMobile: String,
        razorpayOrderId: String,
        razorpayPaymentId: String,
        razorpaySignature: String
    ): Boolean {
        val client = RazorpayClient(apiKey, apiSecret)
        
        val attributes = JSONObject()
        attributes.put("razorpay_order_id", razorpayOrderId)
        attributes.put("razorpay_payment_id", razorpayPaymentId)
        attributes.put("razorpay_signature", razorpaySignature)

        val isValid = Utils.verifyPaymentSignature(attributes, apiSecret)

        if (isValid) {
            val user = userRepository.findByMobileNo(userMobile)
            if (user != null) {
                user.isSubscribed = true
                user.subscriptionExpiry = LocalDateTime.now().plusMonths(1) // 1 month subscription
                userRepository.save(user)
                return true
            }
        }
        return false
    }
}
