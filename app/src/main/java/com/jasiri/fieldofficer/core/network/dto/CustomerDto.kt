package com.jasiri.fieldofficer.core.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Field names mirror springboot-backend's `customers` table / CustomerDto.java (a near 1:1
 * column mirror — see CustomerController `/api/registry/...`). Casing here is based on docs
 * research, not a direct read of the Java source; re-check against the live backend if any
 * response fails to parse.
 */
@Serializable
data class CustomerDto(
    val id: Long? = null,
    val prefix: String? = null,
    @SerialName("Firstname") val firstname: String? = null,
    @SerialName("Surname") val surname: String? = null,
    @SerialName("Middlename") val middlename: String? = null,
    val mobile: String? = null,
    @SerialName("alternative_mobile") val alternativeMobile: String? = null,
    @SerialName("id_number") val idNumber: String? = null,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = null,
    @SerialName("postal_address") val postalAddress: String? = null,
    val town: String? = null,
    val county: String? = null,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("business_type") val businessType: String? = null,
    val industry: String? = null,
    @SerialName("business_location") val businessLocation: String? = null,
    @SerialName("daily_Sales") val dailySales: Double? = null,
    @SerialName("year_established") val yearEstablished: String? = null,
    @SerialName("passport_url") val passportUrl: String? = null,
    @SerialName("id_front_url") val idFrontUrl: String? = null,
    @SerialName("id_back_url") val idBackUrl: String? = null,
    @SerialName("house_image_url") val houseImageUrl: String? = null,
    @SerialName("form_status") val formStatus: String? = null,
    @SerialName("requested_amount") val requestedAmount: Double? = null,
    @SerialName("created_at") val createdAt: String? = null
) {
    val fullName: String
        get() = listOfNotNull(firstname, middlename, surname).joinToString(" ").ifBlank { "Unnamed customer" }

    val isKycComplete: Boolean
        get() = !idFrontUrl.isNullOrBlank() && !idBackUrl.isNullOrBlank() && !passportUrl.isNullOrBlank()
}

@Serializable
data class CustomerListEnvelope(
    val success: Boolean = true,
    val data: CustomerListData? = null
)

@Serializable
data class CustomerListData(
    val customers: List<CustomerDto> = emptyList()
)

@Serializable
data class CustomerBundleEnvelope(
    val success: Boolean = true,
    val data: CustomerBundleDto? = null
)

@Serializable
data class CustomerBundleDto(
    val customer: CustomerDto? = null,
    @SerialName("next_of_kin") val nextOfKin: List<NextOfKinDto> = emptyList(),
    val documents: List<DocumentDto> = emptyList(),
    @SerialName("business_images") val businessImages: List<BusinessImageDto> = emptyList(),
    val loans: List<LoanSummaryDto> = emptyList()
)

@Serializable
data class NextOfKinDto(
    @SerialName("Firstname") val firstname: String? = null,
    @SerialName("Surname") val surname: String? = null,
    val mobile: String? = null
)

@Serializable
data class DocumentDto(
    @SerialName("document_type") val documentType: String? = null,
    @SerialName("document_url") val documentUrl: String? = null
)

@Serializable
data class BusinessImageDto(
    @SerialName("image_url") val imageUrl: String? = null
)

@Serializable
data class LoanSummaryDto(
    val id: Long? = null,
    @SerialName("product_name") val productName: String? = null,
    val balance: Double? = null,
    @SerialName("overdue_amount") val overdueAmount: Double? = null,
    @SerialName("due_date") val dueDate: String? = null
)

@Serializable
data class UploadResultDto(
    val url: String,
    val path: String
)

@Serializable
data class CreateCustomerFields(
    val prefix: String? = null,
    @SerialName("Firstname") val firstname: String,
    @SerialName("Surname") val surname: String,
    @SerialName("Middlename") val middlename: String? = null,
    val mobile: String,
    @SerialName("alternative_mobile") val alternativeMobile: String? = null,
    @SerialName("id_number") val idNumber: String,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    val gender: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = null,
    @SerialName("residence_status") val residenceStatus: String? = null,
    val occupation: String? = null,
    @SerialName("postal_address") val postalAddress: String? = null,
    val code: Int? = null,
    val town: String? = null,
    val county: String? = null,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("business_type") val businessType: String? = null,
    val industry: String? = null,
    @SerialName("business_county") val businessCounty: String? = null,
    @SerialName("business_location") val businessLocation: String? = null,
    val road: String? = null,
    val landmark: String? = null,
    @SerialName("has_local_authority_license") val hasLocalAuthorityLicense: Boolean? = null,
    @SerialName("business_lat") val businessLat: Double? = null,
    @SerialName("business_lng") val businessLng: Double? = null,
    @SerialName("daily_Sales") val dailySales: Double? = null,
    @SerialName("year_established") val yearEstablished: String? = null,
    @SerialName("passport_url") val passportUrl: String? = null,
    @SerialName("id_front_url") val idFrontUrl: String? = null,
    @SerialName("id_back_url") val idBackUrl: String? = null,
    @SerialName("house_image_url") val houseImageUrl: String? = null,
    @SerialName("requested_amount") val requestedAmount: Double? = null,
    @SerialName("form_status") val formStatus: String = "submitted"
)

/**
 * Borrower Security — `security_items` table. `images` is read by the backend as a nested
 * list and written to a separate security_item_images row per URL; it isn't itself a column,
 * so unlike every other field here it's safe regardless of the raw-map-to-SQL-column binding.
 */
@Serializable
data class SecurityItemFields(
    val item: String,
    val description: String? = null,
    val identification: String? = null,
    val value: Double? = null,
    val images: List<String> = emptyList()
)

/**
 * Guarantor Security — `guarantor_security` table. Deliberately a SEPARATE class from
 * SecurityItemFields even though the UI treats them identically: that table has
 * estimated_market_value, not value, and since the backend inserts a request object's keys
 * as literal SQL columns, including a `value` key here would fail the insert outright.
 */
@Serializable
data class GuarantorSecurityItemFields(
    val item: String,
    val description: String? = null,
    val identification: String? = null,
    @SerialName("estimated_market_value") val estimatedMarketValue: Double? = null,
    val images: List<String> = emptyList()
)

/** Mirrors the `guarantors` table exactly — NOT a duplicate of all Personal Info fields. */
@Serializable
data class GuarantorFields(
    val prefix: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = null,
    val gender: String? = null,
    val mobile: String,
    @SerialName("postal_address") val postalAddress: String? = null,
    val code: Int? = null,
    val occupation: String? = null,
    val relationship: String,
    @SerialName("date_of_birth") val dateOfBirth: String? = null,
    @SerialName("residence_status") val residenceStatus: String? = null,
    @SerialName("id_number") val idNumber: String,
    @SerialName("Firstname") val firstname: String,
    @SerialName("Surname") val surname: String,
    @SerialName("Middlename") val middlename: String? = null,
    @SerialName("city_town") val cityTown: String? = null,
    @SerialName("passport_url") val passportUrl: String? = null,
    @SerialName("id_front_url") val idFrontUrl: String? = null,
    @SerialName("id_back_url") val idBackUrl: String? = null,
    val county: String? = null,
    @SerialName("alternative_number") val alternativeNumber: String? = null
)

/** Mirrors the `next_of_kin` table. id_number is an integer column there, unlike the customer's. */
@Serializable
data class NextOfKinFields(
    @SerialName("Firstname") val firstname: String,
    @SerialName("Middlename") val middlename: String? = null,
    @SerialName("Surname") val surname: String,
    @SerialName("id_number") val idNumber: Int? = null,
    val relationship: String,
    @SerialName("relationship_other") val relationshipOther: String? = null,
    val mobile: String,
    @SerialName("alternative_number") val alternativeNumber: String? = null,
    @SerialName("employment_status") val employmentStatus: String,
    @SerialName("company_name") val companyName: String? = null,
    val salary: Double? = null,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("business_income") val businessIncome: Double? = null,
    val county: String,
    @SerialName("city_town") val cityTown: String? = null
)

/** Mirrors the backend's top-level `spouse` map — a sibling of `customer`, not nested in it. */
@Serializable
data class SpouseFields(
    val name: String,
    @SerialName("id_number") val idNumber: String,
    val mobile: String,
    @SerialName("economic_activity") val economicActivity: String? = null
)

@Serializable
data class CreateCustomerRequest(
    val customer: CreateCustomerFields,
    val customerId: Long? = null,
    val spouse: SpouseFields? = null,
    // All of the below are top-level, siblings of `customer` — CustomerServiceImpl.createOrResumeFull
    // reads each as its own key in the same request and inserts into its own table using the
    // customer id resolved in this call. guarantorSecurityItems is only ever attached to the
    // FIRST guarantor in the `guarantors` list (a backend quirk, not a mobile-side limitation).
    val businessImages: List<String>? = null,
    val securityItems: List<SecurityItemFields>? = null,
    val guarantors: List<GuarantorFields>? = null,
    val guarantorSecurityItems: List<GuarantorSecurityItemFields>? = null,
    val nextOfKins: List<NextOfKinFields>? = null
)

@Serializable
data class CreateCustomerResponseDto(
    val success: Boolean = true,
    val data: CreateCustomerResponseData? = null
)

@Serializable
data class CreateCustomerResponseData(
    val customer: CustomerDto? = null,
    val id: Long? = null
)
