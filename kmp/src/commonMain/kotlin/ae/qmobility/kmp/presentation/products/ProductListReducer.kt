package ae.qmobility.kmp.presentation.products

import ae.qmobility.kmp.presentation.model.ProductUi
import ae.qmobility.kmp.presentation.model.UiError
import ae.qmobility.kmp.presentation.mvi.Reducer

sealed interface ProductListTransition {
    data class QueryChanged(val query: String) : ProductListTransition
    data class FirstPageLoading(val query: String) : ProductListTransition
    data class FirstPageLoaded(val products: List<ProductUi>, val nextSkip: Int, val endReached: Boolean) : ProductListTransition
    data class FirstPageFailed(val error: UiError) : ProductListTransition
    data object NextPageLoading : ProductListTransition
    data class NextPageLoaded(val products: List<ProductUi>, val nextSkip: Int, val endReached: Boolean) : ProductListTransition
    data class NextPageFailed(val error: UiError) : ProductListTransition
}

object ProductListReducer : Reducer<ProductListState, ProductListTransition> {
    override fun reduce(state: ProductListState, transition: ProductListTransition): ProductListState = when (transition) {
        is ProductListTransition.QueryChanged -> state.copy(query = transition.query)

        is ProductListTransition.FirstPageLoading -> state.copy(
            activeQuery = transition.query,
            products = emptyList(),
            nextOffset = 0,
            endReached = false,
            isLoading = true,
            isLoadingMore = false,
            error = null,
            loadMoreError = null,
        )

        is ProductListTransition.FirstPageLoaded -> state.copy(
            products = transition.products,
            nextOffset = transition.nextSkip,
            endReached = transition.endReached,
            isLoading = false,
        )

        is ProductListTransition.FirstPageFailed -> state.copy(isLoading = false, error = transition.error)

        ProductListTransition.NextPageLoading -> state.copy(isLoadingMore = true, loadMoreError = null)

        is ProductListTransition.NextPageLoaded -> state.copy(
            products = (state.products + transition.products).distinctBy { it.id },
            nextOffset = transition.nextSkip,
            endReached = transition.endReached,
            isLoadingMore = false,
        )

        is ProductListTransition.NextPageFailed -> state.copy(isLoadingMore = false, loadMoreError = transition.error)
    }
}
