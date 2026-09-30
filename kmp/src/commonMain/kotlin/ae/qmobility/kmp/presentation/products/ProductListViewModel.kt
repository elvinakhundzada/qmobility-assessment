package ae.qmobility.kmp.presentation.products

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.usecase.GetProductPageUseCase
import ae.qmobility.kmp.presentation.model.toUi
import ae.qmobility.kmp.presentation.model.toUiError
import ae.qmobility.kmp.presentation.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProductListViewModel(
    private val getProductPage: GetProductPageUseCase,
) : MviViewModel<ProductListIntent, ProductListState, ProductListTransition, ProductListEffect>(
    initialState = ProductListState(),
    reducer = ProductListReducer,
) {
    private val searchQueryFlow = MutableStateFlow("")

    private var loadJob: Job? = null

    init {
        searchQueryFlow
            .map { it.trim() }
            .debounce { query -> if (query.isEmpty()) 0L else SEARCH_DEBOUNCE_MILLIS }
            .distinctUntilChanged()
            .onEach(::loadFirstPage)
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: ProductListIntent) {
        when (intent) {
            is ProductListIntent.QueryChanged -> {
                reduce(ProductListTransition.QueryChanged(intent.query))
                searchQueryFlow.value = intent.query
            }
            ProductListIntent.LoadNextPage -> loadNextPage()
            ProductListIntent.Retry -> retry()
            is ProductListIntent.ProductClicked -> emitEffect(ProductListEffect.NavigateToDetails(intent.id))
        }
    }

    private fun loadFirstPage(query: String) {
        loadJob?.cancel()
        reduce(ProductListTransition.FirstPageLoading(query))
        loadJob = viewModelScope.launch {
            when (val result = getProductPage(query = query, skip = 0)) {
                is AppResult.Success -> {
                    val page = result.data
                    reduce(
                        ProductListTransition.FirstPageLoaded(
                            products = page.products.map { it.toUi() },
                            nextSkip = page.skip + page.products.size,
                            endReached = !page.hasMore,
                        ),
                    )
                }
                is AppResult.Failure -> reduce(ProductListTransition.FirstPageFailed(result.error.toUiError()))
            }
        }
    }

    private fun loadNextPage() {
        val state = currentState
        if (state.isLoading || state.isLoadingMore || state.endReached || state.error != null || state.loadMoreError != null) return
        startNextPage()
    }

    private fun startNextPage() {
        val state = currentState
        reduce(ProductListTransition.NextPageLoading)
        loadJob = viewModelScope.launch {
            when (val result = getProductPage(query = state.activeQuery, skip = state.nextOffset)) {
                is AppResult.Success -> {
                    val page = result.data
                    reduce(
                        ProductListTransition.NextPageLoaded(
                            products = page.products.map { it.toUi() },
                            nextSkip = page.skip + page.products.size,
                            endReached = !page.hasMore,
                        ),
                    )
                }
                is AppResult.Failure -> reduce(ProductListTransition.NextPageFailed(result.error.toUiError()))
            }
        }
    }

    private fun retry() {
        val state = currentState
        when {
            state.error != null -> loadFirstPage(state.activeQuery)
            state.loadMoreError != null && !state.isLoadingMore -> startNextPage()
        }
    }

    companion object {
        const val SEARCH_DEBOUNCE_MILLIS = 300L
    }
}
