package com.jasiri.fieldofficer.core.routes

import com.jasiri.fieldofficer.core.network.dto.CustomerDto

enum class VisitStatus { PENDING, VISITED }

enum class VisitOutcome(val label: String) {
    PAID("Paid"),
    PROMISE_TO_PAY("Promise to Pay"),
    NOT_FOUND("Not Found"),
    ONBOARDED("Onboarded")
}

data class RouteStop(
    val id: Long,
    val customer: CustomerDto,
    val visitType: String,
    val scheduledTime: String,
    val status: VisitStatus = VisitStatus.PENDING,
    val outcome: VisitOutcome? = null,
    val note: String = ""
)
