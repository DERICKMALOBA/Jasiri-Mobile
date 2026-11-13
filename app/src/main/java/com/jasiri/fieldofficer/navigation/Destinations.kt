package com.jasiri.fieldofficer.navigation

sealed class Destination(val route: String) {
    data object Login : Destination("login")
    data object Home : Destination("home")
    data object Customers : Destination("customers")
    data object Routes : Destination("routes")
    data object Collections : Destination("collections")
    data object Targets : Destination("targets")
    data object AddCustomer : Destination("customers/add")
    data object CustomerDetail : Destination("customers/{customerId}") {
        fun createRoute(customerId: Long) = "customers/$customerId"
    }
}
