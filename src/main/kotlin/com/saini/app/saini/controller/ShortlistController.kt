package com.saini.app.saini.controller

import com.saini.app.saini.data.model.Shortlist
import com.saini.app.saini.repository.ShortlistRepository
import com.saini.app.saini.repository.UserRepository
import com.saini.app.saini.repository.BiodataRepository
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.transaction.annotation.Transactional

@RestController
@RequestMapping("/api/saini/shortlist")
class ShortlistController(
    private val shortlistRepository: ShortlistRepository,
    private val userRepository: UserRepository,
    private val biodataRepository: BiodataRepository
) {

    @PostMapping("/toggle")
    @Transactional
    fun toggleShortlist(
                        @RequestParam(name = "biodataId", required = false) biodataIdParam: Long?,
                        @RequestParam(name = "biodata_id", required = false) biodata_id: Long?,
                        request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val biodataId = biodataIdParam ?: biodata_id ?: return ResponseEntity.badRequest().body(mapOf("success" to false, "message" to "biodataId is required"))

            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()
            val userId = user.id ?: return ResponseEntity.internalServerError().build()

            val existing = shortlistRepository.findByUserIdAndBiodataId(userId, biodataId)
            if (existing != null) {
                shortlistRepository.deleteByUserIdAndBiodataId(userId, biodataId)
                ResponseEntity.ok(mapOf("success" to true, "message" to "Removed from shortlist", "shortlisted" to false))
            } else {
                shortlistRepository.save(Shortlist(userId = userId, biodataId = biodataId))
                ResponseEntity.ok(mapOf("success" to true, "message" to "Added to shortlist", "shortlisted" to true))
            }
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("success" to false, "message" to e.message, "error" to e.toString()))
        }
    }

    @GetMapping("/my")
    fun getMyShortlist(request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val user = userRepository.findByMobileNo(userMobile) ?: return ResponseEntity.notFound().build()
            val userId = user.id ?: return ResponseEntity.internalServerError().build()

            val shortlists = shortlistRepository.findByUserId(userId)
            val biodataIds = shortlists.map { it.biodataId }
            val biodatas = biodataRepository.findAllById(biodataIds)

            ResponseEntity.ok(mapOf("success" to true, "data" to biodatas))
        } catch (e: Exception) {
            ResponseEntity.status(500).body(mapOf("success" to false, "message" to e.message, "error" to e.toString()))
        }
    }
}
