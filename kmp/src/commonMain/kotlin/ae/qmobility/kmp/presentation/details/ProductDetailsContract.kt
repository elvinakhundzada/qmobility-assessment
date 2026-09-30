package ae.qmobility.kmp.presentation.details

import ae.qmobility.kmp.presentation.model.ProductDetailsUi
import ae.qmobility.kmp.presentation.model.UiError

data class ProductDetailsState(
    val isLoading: Boolean = true,
    val product: ProductDetailsUi? = null,
    val isFavorite: Boolean = false,
    val error: UiError? = null,
)

sealed interface ProductDetailsIntent {
    data object ToggleFavorite : ProductDetailsIntent
    data object Retry : ProductDetailsIntent
}

sealed interface ProductDetailsEffect {
    data class FavoriteToggled(val isFavorite: Boolean) : ProductDetailsEffect
}
