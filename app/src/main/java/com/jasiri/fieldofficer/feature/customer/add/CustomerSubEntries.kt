package com.jasiri.fieldofficer.feature.customer.add

import java.io.File

/** UI-layer draft for one Borrower Security or Guarantor Security item (same shape on the web). */
data class SecurityItemEntry(
    val item: String = "",
    val itemOther: String = "",
    val description: String = "",
    val identification: String = "",
    val value: String = "",
    val photos: List<File> = emptyList()
)

/** UI-layer draft for one Guarantor — mirrors the `guarantors` table, not all of Personal Info. */
data class GuarantorEntry(
    val prefix: String = "",
    val firstname: String = "",
    val surname: String = "",
    val middlename: String = "",
    val mobile: String = "",
    val alternativeNumber: String = "",
    val idNumber: String = "",
    val dateOfBirth: String = "",
    val gender: String = "",
    val maritalStatus: String = "",
    val residenceStatus: String = "",
    val occupation: String = "",
    val postalAddress: String = "",
    val postalCode: String = "",
    val county: String = "",
    val cityTown: String = "",
    val relationship: String = "",
    val relationshipOther: String = "",
    val passportFile: File? = null,
    val idFrontFile: File? = null,
    val idBackFile: File? = null,
    // Only ever persisted for the first guarantor in the list — a backend quirk, not a UI one.
    val securityItems: List<SecurityItemEntry> = emptyList()
)

/** UI-layer draft for one Next of Kin — mirrors the `next_of_kin` table. */
data class NextOfKinEntry(
    val firstname: String = "",
    val middlename: String = "",
    val surname: String = "",
    val idNumber: String = "",
    val relationship: String = "",
    val relationshipOther: String = "",
    val mobile: String = "",
    val alternativeNumber: String = "",
    val employmentStatus: String = "",
    val companyName: String = "",
    val salary: String = "",
    val businessName: String = "",
    val businessIncome: String = "",
    val county: String = "",
    val cityTown: String = ""
)
