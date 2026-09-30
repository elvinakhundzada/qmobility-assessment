package ae.qmobility.kmp.presentation.details

import ae.qmobility.kmp.presentation.model.ProductDetailsUi
import ae.qmobility.kmp.presentation.model.UiError
import ae.qmobility.kmp.presentation.mvi.Reducer

sealed interface ProductDetailsTransition {
    data object Loading : ProductDetailsTransition
    data class Loaded(val product: ProductDetailsUi) : ProductDetailsTransition
    data class Failed(val error: UiError) : ProductDetailsTransition
    data class ProductFavoriteStatusUpdated(val isFavorite: Boolean) : ProductDetailsTransition
}

object ProductDetailsReducer : Reducer<ProductDetailsState, ProductDetailsTransition> {
    override fun reduce(state: ProductDetailsState, transition: ProductDetailsTransition): ProductDetailsState = when (transition) {
        ProductDetailsTransition.Loading -> state.copy(isLoading = true, error = null)
        is ProductDetailsTransition.Loaded -> state.copy(isLoading = false, product = transition.product)
        is ProductDetailsTransition.Failed -> state.copy(isLoading = false, error = transition.error)
        is ProductDetailsTransition.ProductFavoriteStatusUpdated -> state.copy(isFavorite = transition.isFavorite)
    }
}
