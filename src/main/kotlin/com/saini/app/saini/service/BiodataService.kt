package com.saini.app.saini.service

import com.saini.app.saini.data.model.Biodata
import com.saini.app.saini.repository.BiodataRepository
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*

@Service
class BiodataService(private val biodataRepository: BiodataRepository) {

    private val uploadDir = "uploads/biodata_photos/"

    fun saveBiodata(biodata: Biodata, photos: Array<MultipartFile>?, userMobile: String): Biodata {
        val photoUrls = mutableListOf<String>()

        val uploadFolder = File(uploadDir)
        if (!uploadFolder.exists()) {
            uploadFolder.mkdirs()
        }

        photos?.forEach { photo ->
            if (!photo.isEmpty) {
                val fileName = UUID.randomUUID().toString() + "_" + photo.originalFilename
                val path = Paths.get(uploadDir + fileName)
                Files.write(path, photo.bytes)
                
                // Construct URL - Replace with your actual server IP/domain if necessary
                val url = "http://192.168.0.112:8080/uploads/biodata_photos/$fileName"
                photoUrls.add(url)
            }
        }

        biodata.profilePicUrls = photoUrls
        biodata.createdBy = userMobile
        return biodataRepository.save(biodata)
    }

    fun getAllBiodata(userMobile: String): List<Biodata> {
        return biodataRepository.findByCreatedByNot(userMobile)
    }

    fun getMyBiodata(userMobile: String): List<Biodata> {
        return biodataRepository.findByCreatedBy(userMobile)
    }

    fun searchBiodatas(userMobile: String, query: String?, gender: String?, maritalStatus: String?): List<Biodata> {
        return biodataRepository.searchBiodatas(userMobile, query, gender, maritalStatus)
    }

    fun updateBiodata(id: Long, updatedBiodata: Biodata, photos: Array<MultipartFile>?, userMobile: String): Biodata {
        val existingBiodata = biodataRepository.findById(id)
            .orElseThrow { Exception("Biodata not found") }

        if (existingBiodata.createdBy != userMobile) {
            throw Exception("Unauthorized to update this biodata")
        }

        // Update fields
        existingBiodata.apply {
            fullName = updatedBiodata.fullName
            gender = updatedBiodata.gender
            dob = updatedBiodata.dob
            birthTime = updatedBiodata.birthTime
            birthPlace = updatedBiodata.birthPlace
            height = updatedBiodata.height
            complexion = updatedBiodata.complexion
            gotra = updatedBiodata.gotra
            qualification = updatedBiodata.qualification
            university = updatedBiodata.university
            occupation = updatedBiodata.occupation
            company = updatedBiodata.company
            income = updatedBiodata.income
            jobLocation = updatedBiodata.jobLocation
            fatherName = updatedBiodata.fatherName
            fatherOccupation = updatedBiodata.fatherOccupation
            motherName = updatedBiodata.motherName
            motherOccupation = updatedBiodata.motherOccupation
            siblings = updatedBiodata.siblings
            nativePlace = updatedBiodata.nativePlace
            contactNumber = updatedBiodata.contactNumber
        }

        // Handle new photos if provided
        if (photos != null && photos.isNotEmpty() && !photos[0].isEmpty) {
            val photoUrls = mutableListOf<String>()
            val uploadFolder = File(uploadDir)
            if (!uploadFolder.exists()) uploadFolder.mkdirs()

            photos.forEach { photo ->
                if (!photo.isEmpty) {
                    val fileName = UUID.randomUUID().toString() + "_" + photo.originalFilename
                    val path = Paths.get(uploadDir + fileName)
                    Files.write(path, photo.bytes)
                    val url = "http://192.168.0.112:8080/uploads/biodata_photos/$fileName"
                    photoUrls.add(url)
                }
            }
            existingBiodata.profilePicUrls = photoUrls
        }

        return biodataRepository.save(existingBiodata)
    }
}
