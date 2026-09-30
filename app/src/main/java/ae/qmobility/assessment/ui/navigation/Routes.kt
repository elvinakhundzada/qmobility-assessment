package ae.qmobility.assessment.ui.navigation

import kotlinx.serialization.Serializable

@Serializable
data object ProductsRoute

@Serializable
data object FavoritesRoute
@Serializable
data class ProductDetailsRoute(val productId: Long)
