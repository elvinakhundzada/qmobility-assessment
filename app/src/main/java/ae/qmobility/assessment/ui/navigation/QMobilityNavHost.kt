package ae.qmobility.assessment.ui.navigation

import ae.qmobility.assessment.R
import ae.qmobility.assessment.ui.details.ProductDetailsScreen
import ae.qmobility.assessment.ui.favorites.FavoritesScreen
import ae.qmobility.assessment.ui.products.ProductListScreen
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import kotlin.reflect.KClass

private enum class TopLevelTab(
    val route: Any,
    val routeClass: KClass<*>,
    val selectedIcon: ImageVector,
    val icon: ImageVector,
    val label: Int,
) {
    Products(ProductsRoute, ProductsRoute::class, Icons.Filled.Home, Icons.Outlined.Home, R.string.tab_products),
    Favorites(
        FavoritesRoute,
        FavoritesRoute::class,
        Icons.Filled.Favorite,
        Icons.Outlined.FavoriteBorder,
        R.string.tab_favorites,
    ),
}

@Composable
fun QMobilityNavHost(navController: NavHostController = rememberNavController()) {
    val backStackEntry by navController.currentBackStackEntryAsState()
    val destination = backStackEntry?.destination
    val isTopLevel = TopLevelTab.entries.any { tab -> destination?.hasRoute(tab.routeClass) == true }

    Scaffold(
        bottomBar = {
            if (isTopLevel) {
                BottomNavigationMenu(destination = destination, onTabSelected = { navController.navigateToTab(it.route) })
            }
        },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val bottomBarPadding = PaddingValues(bottom = padding.calculateBottomPadding())
        NavHost(
            navController = navController,
            startDestination = ProductsRoute,
            modifier = Modifier.padding(bottomBarPadding).consumeWindowInsets(bottomBarPadding),
        ) {
            composable<ProductsRoute> {
                ProductListScreen(onOpenDetails = { navController.navigate(ProductDetailsRoute(it)) })
            }
            composable<FavoritesRoute> {
                FavoritesScreen(onOpenDetails = { navController.navigate(ProductDetailsRoute(it)) })
            }
            composable<ProductDetailsRoute> { entry ->
                ProductDetailsScreen(
                    productId = entry.toRoute<ProductDetailsRoute>().productId,
                    onBack = navController::navigateUp,
                )
            }
        }
    }
}

@Composable
private fun BottomNavigationMenu(destination: NavDestination?, onTabSelected: (TopLevelTab) -> Unit) {
    Surface(
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp,
    ) {
        NavigationBar(containerColor = Color.Transparent, tonalElevation = 0.dp) {
            TopLevelTab.entries.forEach { tab ->
                val selected = destination?.hierarchy?.any { it.hasRoute(tab.routeClass) } == true
                NavigationBarItem(
                    selected = selected,
                    onClick = { onTabSelected(tab) },
                    icon = { Icon(if (selected) tab.selectedIcon else tab.icon, contentDescription = null) },
                    label = {
                        Text(
                            stringResource(tab.label),
                            fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Normal,
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.secondaryContainer,
                        unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    ),
                )
            }
        }
    }
}

private fun NavHostController.navigateToTab(route: Any) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) { saveState = true }
        launchSingleTop = true
        restoreState = true
    }
}
