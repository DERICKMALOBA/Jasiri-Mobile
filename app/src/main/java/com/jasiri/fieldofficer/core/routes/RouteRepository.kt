package com.jasiri.fieldofficer.core.routes

import com.jasiri.fieldofficer.core.network.dto.CustomerDto
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Backend has no route-planning endpoint yet (see docs/Field-Officer-Mobile-App-Plan.pdf §4) —
 * the officer picks which of their own customers to visit (see SelectVisitCustomersScreen) and
 * that selection is kept here, in memory, until the real /api/officer/route endpoint ships.
 */
@Singleton
class RouteRepository @Inject constructor() {
    private val _stops = MutableStateFlow<List<RouteStop>>(emptyList())
    val stops: StateFlow<List<RouteStop>> = _stops

    private val visitTypes = listOf("Collection", "Onboarding", "Follow-up", "Collection", "Onboarding")
    private val scheduledTimes = listOf("8:30am", "9:15am", "10:00am", "11:00am", "1:30pm")

    /** Replaces today's route with the given customers, in the order selected. */
    fun planStops(customers: List<CustomerDto>) {
        _stops.value = customers.mapIndexed { index, customer ->
            RouteStop(
                id = customer.id ?: index.toLong(),
                customer = customer,
                visitType = visitTypes[index % visitTypes.size],
                scheduledTime = scheduledTimes[index % scheduledTimes.size]
            )
        }
    }

    fun markVisited(stopId: Long, outcome: VisitOutcome, note: String) {
        _stops.value = _stops.value.map { stop ->
            if (stop.id == stopId) stop.copy(status = VisitStatus.VISITED, outcome = outcome, note = note) else stop
        }
    }
}
