package ae.qmobility.kmp.presentation.products

import ae.qmobility.kmp.presentation.model.ProductUi
import ae.qmobility.kmp.presentation.model.UiError

data class ProductListState(
    val query: String = "",
    val activeQuery: String = "",
    val products: List<ProductUi> = emptyList(),
    val nextOffset: Int = 0,
    val endReached: Boolean = false,
    val isLoading: Boolean = true,
    val isLoadingMore: Boolean = false,
    val error: UiError? = null,
    val loadMoreError: UiError? = null,
) {
    val isEmpty: Boolean get() = !isLoading && error == null && products.isEmpty()
}

sealed interface ProductListIntent {
    data class QueryChanged(val query: String) : ProductListIntent
    data object LoadNextPage : ProductListIntent
    data object Retry : ProductListIntent
    data class ProductClicked(val id: Long) : ProductListIntent
}

sealed interface ProductListEffect {
    data class NavigateToDetails(val id: Long) : ProductListEffect
}
