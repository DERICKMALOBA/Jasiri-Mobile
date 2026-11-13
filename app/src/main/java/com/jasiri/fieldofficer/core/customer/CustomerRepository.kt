package com.jasiri.fieldofficer.core.customer

import com.jasiri.fieldofficer.core.network.CustomerApi
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerFields
import com.jasiri.fieldofficer.core.network.dto.CreateCustomerRequest
import com.jasiri.fieldofficer.core.network.dto.CustomerBundleDto
import com.jasiri.fieldofficer.core.network.dto.CustomerDto
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
    }

    suspend fun getCustomerFull(id: Long): Result<CustomerBundleDto> = runCatching {
        api.getCustomerFull(id).data ?: error("Customer not found")
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
