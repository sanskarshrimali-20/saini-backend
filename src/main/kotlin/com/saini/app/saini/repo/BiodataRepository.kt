package com.saini.app.saini.repository

import com.saini.app.saini.data.model.Biodata
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface BiodataRepository : JpaRepository<Biodata, Long> {
    fun findByCreatedBy(createdBy: String): List<Biodata>
    fun findByCreatedByNot(createdBy: String): List<Biodata>

    @Query("SELECT b FROM Biodata b WHERE b.createdBy != :userMobile AND " +
           "(:query IS NULL OR b.fullName LIKE %:query% OR b.nativePlace LIKE %:query% OR b.jobLocation LIKE %:query%) AND " +
           "(:gender IS NULL OR b.gender = :gender) AND " +
           "(:maritalStatus IS NULL OR b.maritalStatus = :maritalStatus)")
    fun searchBiodatas(
        @Param("userMobile") userMobile: String,
        @Param("query") query: String?,
        @Param("gender") gender: String?,
        @Param("maritalStatus") maritalStatus: String?
    ): List<Biodata>
}
