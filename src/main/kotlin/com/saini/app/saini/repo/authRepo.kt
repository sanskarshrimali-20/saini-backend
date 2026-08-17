package com.saini.app.saini.repository

import com.saini.app.saini.data.model.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<User, Long> {
fun findByMobileNo(mobileNo: String): User?
}