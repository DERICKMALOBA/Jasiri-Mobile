package com.jasiri.fieldofficer.core.customer

import com.jasiri.fieldofficer.BuildConfig
import com.jasiri.fieldofficer.core.network.CustomerApi
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerFields
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerRequest
import com.jasiri.fieldofficer.core.network.dto.CustomerBundleDto
import com.jasiri.fieldofficer.core.network.dto.CustomerDto
import com.jasiri.fieldofficer.core.network.dto.LoanSummaryDto
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import java.security.MessageDigest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CustomerRepository @Inject constructor(
    private val api: CustomerApi
) {
    suspend fun listCustomers(query: String? = null): Result<List<CustomerDto>> = runCatching {
        val envelope = if (query.isNullOrBlank()) api.listCustomers() else api.searchCustomers(query)
        envelope.data?.customers ?: emptyList()
    }.recoverIfDebug { mockCustomers }

    suspend fun getCustomerFull(id: Long): Result<CustomerBundleDto> = runCatching {
        api.getCustomerFull(id).data ?: error("Customer not found")
    }.recoverIfDebug { mockBundle(id) }

    // Backend DB isn't reachable yet — in debug builds, fall back to clearly-fake local
    // data instead of surfacing the backend's DB error, so screens stay populated while
    // the backend is unavailable. Never applies to release builds.
    private inline fun <T> Result<T>.recoverIfDebug(fallback: () -> T): Result<T> =
        if (isFailure && BuildConfig.DEBUG) Result.success(fallback()) else this

    private val mockCustomers: List<CustomerDto>
        get() = listOf(
            CustomerDto(
                id = 1001L, firstname = "Amina", surname = "Otieno",
                mobile = "0712345001", town = "Kisumu", county = "Kisumu",
                businessName = "Amina's Grocery", businessType = "Retail",
                idFrontUrl = "mock", idBackUrl = "mock", passportUrl = "mock",
                requestedAmount = 25000.0
            ),
            CustomerDto(
                id = 1002L, firstname = "Brian", surname = "Mutiso",
                mobile = "0712345002", town = "Machakos", county = "Machakos",
                businessName = "Mutiso Hardware", businessType = "Wholesale",
                requestedAmount = 40000.0
            ),
            CustomerDto(
                id = 1003L, firstname = "Cynthia", surname = "Wanjiru",
                mobile = "0712345003", town = "Nakuru", county = "Nakuru",
                businessName = "Wanjiru Salon", businessType = "Services",
                idFrontUrl = "mock", idBackUrl = "mock", passportUrl = "mock",
                requestedAmount = 15000.0
            )
        )

    private fun mockBundle(id: Long): CustomerBundleDto {
        val customer = mockCustomers.find { it.id == id } ?: mockCustomers.first().copy(id = id)
        return CustomerBundleDto(
            customer = customer,
            loans = listOf(
                LoanSummaryDto(
                    id = id * 10,
                    productName = "Business Growth Loan",
                    balance = 18500.0,
                    overdueAmount = 0.0,
                    dueDate = "2026-11-28"
                )
            )
        )
    }

    suspend fun uploadPhoto(file: File, bucket: String): Result<String> = runCatching {
        val contentHash = sha256(file)
        val filePart = MultipartBody.Part.createFormData(
            "file", file.name, file.asRequestBody("image/jpeg".toMediaType())
        )
        val result = api.uploadFile(
            file = filePart,
            bucket = bucket.toRequestBody("text/plain".toMediaType()),
            path = "field-officer/${file.name}".toRequestBody("text/plain".toMediaType()),
            contentHash = contentHash.toRequestBody("text/plain".toMediaType())
        )
        result.url
    }

    suspend fun createCustomer(fields: CreateCustomerFields): Result<CustomerDto?> = runCatching {
        api.createCustomerFull(CreateCustomerRequest(customer = fields)).data?.customer
    }

    private fun sha256(file: File): String {
        val digest = MessageDigest.getInstance("SHA-256")
        file.inputStream().use { input ->
            val buffer = ByteArray(8192)
            var read: Int
            while (input.read(buffer).also { read = it } > 0) {
                digest.update(buffer, 0, read)
            }
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }
}
