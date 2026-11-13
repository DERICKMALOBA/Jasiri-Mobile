package com.jasiri.fieldofficer.navigation

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Home : Destination("home")
    data object Customers : Destination("customers")
    data object Routes : Destination("routes")
    data object Collections : Destination("collections")
    data object Targets : Destination("targets")
    data object AddCustomer : Destination("customers/add")
    data object AddLead : Destination("leads/add")
    data object VisitOutcome : Destination("routes/{stopId}/visit") {
        fun createRoute(stopId: Long) = "routes/$stopId/visit"
    }
    data object SelectVisitCustomers : Destination("routes/select")
    data object CollectPayment : Destination("collections/pay?phone={phone}&amount={amount}") {
        fun createRoute(phone: String = "", amount: String = "") = "collections/pay?phone=$phone&amount=$amount"
    }
    data object CustomerDetail : Destination("customers/{customerId}") {
        fun createRoute(customerId: Long) = "customers/$customerId"
    }
}
