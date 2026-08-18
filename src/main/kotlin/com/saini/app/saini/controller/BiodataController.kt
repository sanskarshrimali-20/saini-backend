package com.saini.app.saini.controller

import com.saini.app.saini.data.model.Biodata
import com.saini.app.saini.service.BiodataService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/saini/biodata")
class BiodataController(private val biodataService: BiodataService) {

    @PostMapping("/create")
    fun createBiodata(
        @RequestParam("fullName") fullName: String,
        @RequestParam("gender") gender: String,
        @RequestParam("dob") dob: String,
        @RequestParam("birthTime") birthTime: String,
        @RequestParam("birthPlace") birthPlace: String,
        @RequestParam("height") height: String,
        @RequestParam("maritalStatus") maritalStatus: String,
        @RequestParam("complexion") complexion: String,
        @RequestParam("gotra") gotra: String,
        @RequestParam("qualification") qualification: String,
        @RequestParam("university") university: String,
        @RequestParam("occupation") occupation: String,
        @RequestParam("company") company: String,
        @RequestParam("income") income: String,
        @RequestParam("jobLocation") jobLocation: String,
        @RequestParam("fatherName") fatherName: String,
        @RequestParam("fatherOccupation") fatherOccupation: String,
        @RequestParam("motherName") motherName: String,
        @RequestParam("motherOccupation") motherOccupation: String,
        @RequestParam("siblings") siblings: String,
        @RequestParam("nativePlace") nativePlace: String,
        @RequestParam("contactNumber") contactNumber: String,
        @RequestParam("photos", required = false) photos: List<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            
            println("Creating biodata for $userMobile - Name: $fullName, Photos: ${photos?.size ?: 0}")
            
            val biodata = Biodata(
                fullName = fullName,
                gender = gender,
                dob = dob,
                birthTime = birthTime,
                birthPlace = birthPlace,
                height = height,
                maritalStatus = maritalStatus,
                complexion = complexion,
                gotra = gotra,
                qualification = qualification,
                university = university,
                occupation = occupation,
                company = company,
                income = income,
                jobLocation = jobLocation,
                fatherName = fatherName,
                fatherOccupation = fatherOccupation,
                motherName = motherName,
                motherOccupation = motherOccupation,
                siblings = siblings,
                nativePlace = nativePlace,
                contactNumber = contactNumber
            )

            val savedBiodata = biodataService.saveBiodata(biodata, photos, userMobile)
            ResponseEntity.ok(mapOf("success" to true, "data" to savedBiodata))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }

    @PostMapping("/update/{id}")
    fun updateBiodata(
        @PathVariable id: Long,
        @RequestParam("fullName") fullName: String,
        @RequestParam("gender") gender: String,
        @RequestParam("dob") dob: String,
        @RequestParam("birthTime") birthTime: String,
        @RequestParam("birthPlace") birthPlace: String,
        @RequestParam("height") height: String,
        @RequestParam("maritalStatus") maritalStatus: String,
        @RequestParam("complexion") complexion: String,
        @RequestParam("gotra") gotra: String,
        @RequestParam("qualification") qualification: String,
        @RequestParam("university") university: String,
        @RequestParam("occupation") occupation: String,
        @RequestParam("company") company: String,
        @RequestParam("income") income: String,
        @RequestParam("jobLocation") jobLocation: String,
        @RequestParam("fatherName") fatherName: String,
        @RequestParam("fatherOccupation") fatherOccupation: String,
        @RequestParam("motherName") motherName: String,
        @RequestParam("motherOccupation") motherOccupation: String,
        @RequestParam("siblings") siblings: String,
        @RequestParam("nativePlace") nativePlace: String,
        @RequestParam("contactNumber") contactNumber: String,
        @RequestParam("photos") photos: Array<MultipartFile>?,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            
            val biodata = Biodata(
                fullName = fullName,
                gender = gender,
                dob = dob,
                birthTime = birthTime,
                birthPlace = birthPlace,
                height = height,
                maritalStatus = maritalStatus,
                complexion = complexion,
                gotra = gotra,
                qualification = qualification,
                university = university,
                occupation = occupation,
                company = company,
                income = income,
                jobLocation = jobLocation,
                fatherName = fatherName,
                fatherOccupation = fatherOccupation,
                motherName = motherName,
                motherOccupation = motherOccupation,
                siblings = siblings,
                nativePlace = nativePlace,
                contactNumber = contactNumber
            )

            val updated = biodataService.updateBiodata(id, biodata, photos, userMobile)
            ResponseEntity.ok(mapOf("success" to true, "data" to updated))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }

    @GetMapping("/all")
    fun getAllBiodata(request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val list = biodataService.getAllBiodata(userMobile)
            ResponseEntity.ok(mapOf("success" to true, "data" to list))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }

    @GetMapping("/my-biodata")
    fun getMyBiodata(request: HttpServletRequest): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val list = biodataService.getMyBiodata(userMobile)
            ResponseEntity.ok(mapOf("success" to true, "data" to list))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }

    @GetMapping("/search")
    fun searchBiodatas(
        @RequestParam(required = false) query: String?,
        @RequestParam(required = false) gender: String?,
        @RequestParam(required = false) maritalStatus: String?,
        request: HttpServletRequest
    ): ResponseEntity<Any> {
        return try {
            val userMobile = request.getAttribute("userMobile")?.toString() ?: return ResponseEntity.status(401).body("Unauthorized")
            val list = biodataService.searchBiodatas(userMobile, query, gender, maritalStatus)
            ResponseEntity.ok(mapOf("success" to true, "data" to list))
        } catch (e: Exception) {
            ResponseEntity.internalServerError().body(mapOf("success" to false, "message" to e.message))
        }
    }
}
