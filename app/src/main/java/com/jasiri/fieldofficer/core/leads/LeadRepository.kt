package com.jasiri.fieldofficer.core.leads

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.util.concurrent.atomic.AtomicLong
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In-memory only — the backend has no leads endpoint yet (same gap as DashboardApi's
 * /api/officer/dashboard). Seeded with placeholder leads so the dashboard's Leads count
 * matches DashboardRepository's PLACEHOLDER until both are backed by the real API.
 */
@Singleton
class LeadRepository @Inject constructor() {

    private val nextId = AtomicLong(1_000)

    private val _leads = MutableStateFlow(seedLeads())
    val leads: StateFlow<List<Lead>> = _leads

    fun addLead(name: String, mobile: String, businessIdea: String, notes: String = ""): Lead {
        val lead = Lead(id = nextId.incrementAndGet(), name = name, mobile = mobile, businessIdea = businessIdea, notes = notes)
        _leads.value = _leads.value + lead
        return lead
    }

    private fun seedLeads(): List<Lead> = (1..12).map { n ->
        Lead(id = nextId.incrementAndGet(), name = "Lead #$n", mobile = "", businessIdea = "")
    }
}
