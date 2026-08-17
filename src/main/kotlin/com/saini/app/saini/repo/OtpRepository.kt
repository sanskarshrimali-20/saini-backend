package com.saini.app.saini.repository

import com.saini.app.saini.data.model.OtpCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface OtpRepository : JpaRepository<OtpCode, Long> {
    fun findTopByMobileNoOrderByExpiryTimeDesc(mobileNo: String): OtpCode?
    fun deleteByMobileNo(mobileNo: String)
}
