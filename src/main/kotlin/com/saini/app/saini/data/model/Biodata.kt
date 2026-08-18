package com.saini.app.saini.data.model

import jakarta.persistence.*

@Entity
@Table(name = "biodatas")
data class Biodata(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Personal Details
    var fullName: String = "",
    var gender: String = "",
    var dob: String = "",
    var birthTime: String = "",
    var birthPlace: String = "",
    var height: String = "",
    var maritalStatus: String = "",
    var complexion: String = "",
    var gotra: String = "",

    // Education & Career
    var qualification: String = "",
    var university: String = "",
    var occupation: String = "",
    var company: String = "",
    var income: String = "",
    var jobLocation: String = "",

    // Family Details
    var fatherName: String = "",
    var fatherOccupation: String = "",
    var motherName: String = "",
    var motherOccupation: String = "",
    var siblings: String = "",
    var nativePlace: String = "",
    var contactNumber: String = "",

    // Photo URLs (Stored as a collection)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "biodata_photos", joinColumns = [JoinColumn(name = "biodata_id")])
    @Column(name = "photo_url")
    var profilePicUrls: List<String> = mutableListOf(),

    // Identification
    var createdBy: String = ""
)
