package com.jasiri.fieldofficer.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.jasiri.fieldofficer.feature.auth.LoginScreen
import com.jasiri.fieldofficer.feature.collections.CollectPaymentScreen
import com.jasiri.fieldofficer.feature.common.ComingSoonScreen
import com.jasiri.fieldofficer.feature.customer.add.AddCustomerScreen
import com.jasiri.fieldofficer.feature.customer.detail.CustomerDetailScreen
import com.jasiri.fieldofficer.feature.customer.list.CustomerListScreen
import com.jasiri.fieldofficer.feature.dashboard.DashboardScreen
import com.jasiri.fieldofficer.feature.leads.AddLeadScreen
import com.jasiri.fieldofficer.feature.routes.RoutesScreen
import com.jasiri.fieldofficer.feature.routes.SelectVisitCustomersScreen
import com.jasiri.fieldofficer.feature.routes.VisitOutcomeScreen

private data class BottomNavItem(val destination: Destination, val icon: ImageVector, val label: String)

private val bottomNavItems = listOf(
    BottomNavItem(Destination.Home, Icons.Filled.Home, "Home"),
    BottomNavItem(Destination.Customers, Icons.Filled.People, "Customers"),
    BottomNavItem(Destination.Routes, Icons.Filled.Place, "Routes"),
    BottomNavItem(Destination.Collections, Icons.Filled.AccountBalanceWallet, "Collect"),
    BottomNavItem(Destination.Targets, Icons.Filled.TrackChanges, "Targets"),
)

private val bottomNavRoutes = bottomNavItems.map { it.destination.route }.toSet()

@Composable
fun FieldOfficerNavGraph(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavRoutes) {
                FieldOfficerBottomBar(navController, currentRoute)
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            // TODO: restore Destination.Login.route as startDestination once login flow needs to be enforced again.
            startDestination = Destination.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Destination.Login.route) {
                LoginScreen(
                    onLoginSuccess = {
                        navController.navigate(Destination.Home.route) {
                            popUpTo(Destination.Login.route) { inclusive = true }
                        }
                    }
                )
            }
            composable(Destination.Home.route) {
                DashboardScreen(
                    onNewCustomer = { navController.navigate(Destination.AddCustomer.route) },
                    onNewLead = { navController.navigate(Destination.AddLead.route) },
                    onStartRoute = {
                        navController.navigate(Destination.Routes.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    },
                    onCollectPayment = {
                        navController.navigate(Destination.Collections.route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                )
            }
            composable(Destination.Customers.route) {
                CustomerListScreen(
                    onAddCustomer = { navController.navigate(Destination.AddCustomer.route) },
                    onCustomerClick = { customerId ->
                        navController.navigate(Destination.CustomerDetail.createRoute(customerId))
                    }
                )
            }
            composable(Destination.AddCustomer.route) {
                AddCustomerScreen(
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Destination.AddLead.route) {
                AddLeadScreen(
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(
                Destination.CustomerDetail.route,
                arguments = listOf(navArgument("customerId") { type = NavType.LongType })
            ) { backStackEntry ->
                val customerId = backStackEntry.arguments?.getLong("customerId") ?: 0L
                CustomerDetailScreen(customerId = customerId, onBack = { navController.popBackStack() })
            }
            composable(Destination.Routes.route) {
                RoutesScreen(
                    onArrive = { stopId -> navController.navigate(Destination.VisitOutcome.createRoute(stopId)) },
                    onPlanVisits = { navController.navigate(Destination.SelectVisitCustomers.route) }
                )
            }
            composable(
                Destination.VisitOutcome.route,
                arguments = listOf(navArgument("stopId") { type = NavType.LongType })
            ) {
                VisitOutcomeScreen(onBack = { navController.popBackStack() })
            }
            composable(Destination.SelectVisitCustomers.route) {
                SelectVisitCustomersScreen(
                    onDone = { navController.popBackStack() },
                    onCancel = { navController.popBackStack() }
                )
            }
            composable(Destination.Collections.route) { CollectPaymentScreen() }
            composable(Destination.Targets.route) { ComingSoonScreen("Targets") }
        }
    }
}

@Composable
private fun FieldOfficerBottomBar(navController: NavHostController, currentRoute: String?) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                selected = currentRoute == item.destination.route,
                onClick = {
                    navController.navigate(item.destination.route) {
                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                icon = { Icon(item.icon, contentDescription = item.label) },
                label = {
                    Text(
                        item.label,
                        maxLines = 1,
                        softWrap = false,
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            )
        }
    }
}
