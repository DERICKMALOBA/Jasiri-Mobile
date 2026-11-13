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
    @SerialName("id_number") val idNumber: String,
    val gender: String? = null,
    @SerialName("marital_status") val maritalStatus: String? = null,
    val town: String? = null,
    val county: String? = null,
    @SerialName("business_name") val businessName: String? = null,
    @SerialName("business_type") val businessType: String? = null,
    val industry: String? = null,
    @SerialName("business_location") val businessLocation: String? = null,
    @SerialName("daily_Sales") val dailySales: Double? = null,
    @SerialName("passport_url") val passportUrl: String? = null,
    @SerialName("id_front_url") val idFrontUrl: String? = null,
    @SerialName("id_back_url") val idBackUrl: String? = null,
    @SerialName("form_status") val formStatus: String = "submitted"
)

@Serializable
data class CreateCustomerRequest(
    val customer: CreateCustomerFields,
    val customerId: Long? = null
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
