package com.saini.app.saini.data.model

import jakarta.persistence.*

@Entity
@Table(name = "shortlist") // or your table name
class Shortlist(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "user_id", nullable = false)
    val userId: Long = 0,

    @Column(name = "biodata_id", nullable = false)
    val biodataId: Long = 0
)
