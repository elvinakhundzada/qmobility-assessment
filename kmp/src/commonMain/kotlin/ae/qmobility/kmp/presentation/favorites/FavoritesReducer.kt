package ae.qmobility.kmp.presentation.favorites

import ae.qmobility.kmp.presentation.model.ProductUi
import ae.qmobility.kmp.presentation.mvi.Reducer

sealed interface FavoritesTransition {
    data class Loaded(val products: List<ProductUi>) : FavoritesTransition
}

object FavoritesReducer : Reducer<FavoritesState, FavoritesTransition> {
    override fun reduce(state: FavoritesState, transition: FavoritesTransition): FavoritesState = when (transition) {
        is FavoritesTransition.Loaded -> state.copy(isLoading = false, products = transition.products)
    }
}
