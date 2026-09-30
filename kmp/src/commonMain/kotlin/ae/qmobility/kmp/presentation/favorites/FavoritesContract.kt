package ae.qmobility.kmp.presentation.favorites

import ae.qmobility.kmp.presentation.model.ProductUi

data class FavoritesState(
    val isLoading: Boolean = true,
    val products: List<ProductUi> = emptyList(),
) {
    val isEmpty: Boolean get() = !isLoading && products.isEmpty()
}

sealed interface FavoritesIntent {
    data class ProductClicked(val id: Long) : FavoritesIntent
    data class RemoveClicked(val id: Long) : FavoritesIntent
}

sealed interface FavoritesEffect {
    data class NavigateToDetails(val id: Long) : FavoritesEffect
}
