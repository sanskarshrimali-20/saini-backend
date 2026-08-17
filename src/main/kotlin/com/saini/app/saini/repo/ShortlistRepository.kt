package com.saini.app.saini.repository

import com.saini.app.saini.data.model.Shortlist
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface ShortlistRepository : JpaRepository<Shortlist, Long> {
    fun findByUserId(userId: Long): List<Shortlist>
    fun findByUserIdAndBiodataId(userId: Long, biodataId: Long): Shortlist?

    @Modifying
    @Query("DELETE FROM Shortlist s WHERE s.userId = :userId AND s.biodataId = :biodataId")
    fun deleteByUserIdAndBiodataId(userId: Long, biodataId: Long)
}
