package com.example.inventorycotrol.ui.navigation

import androidx.navigation.NavOptionsBuilder

sealed class NavigationAction {

    data class Navigate(
        val destination: Destination,
        val navOptions: NavOptionsBuilder.() -> Unit = {},
    ) : NavigationAction()

    data class NavigateUp(
        val args: Map<String, Any?> = emptyMap(),
    ): NavigationAction()

    data object OpenNavigationDrawer: NavigationAction()

}