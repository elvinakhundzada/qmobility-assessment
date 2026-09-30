package ae.qmobility.kmp.presentation.details

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.usecase.GetProductDetailsUseCase
import ae.qmobility.kmp.domain.usecase.ObserveIsFavoriteUseCase
import ae.qmobility.kmp.domain.usecase.ToggleFavoriteUseCase
import ae.qmobility.kmp.presentation.model.toDetailsUi
import ae.qmobility.kmp.presentation.model.toUiError
import ae.qmobility.kmp.presentation.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class ProductDetailsViewModel(
    private val productId: Long,
    private val getProductDetails: GetProductDetailsUseCase,
    observeIsFavorite: ObserveIsFavoriteUseCase,
    private val toggleFavorite: ToggleFavoriteUseCase,
) : MviViewModel<ProductDetailsIntent, ProductDetailsState, ProductDetailsTransition, ProductDetailsEffect>(
    initialState = ProductDetailsState(),
    reducer = ProductDetailsReducer,
) {
    private var product: Product? = null
    private var loadJob: Job? = null
    private var toggleJob: Job? = null

    init {
        observeIsFavorite(productId)
            .onEach { reduce(ProductDetailsTransition.ProductFavoriteStatusUpdated(it)) }
            .launchIn(viewModelScope)
        load()
    }

    override fun onIntent(intent: ProductDetailsIntent) {
        when (intent) {
            ProductDetailsIntent.ToggleFavorite -> toggle()
            ProductDetailsIntent.Retry -> load()
        }
    }

    private fun load() {
        if (loadJob?.isActive == true) return
        reduce(ProductDetailsTransition.Loading)
        loadJob = viewModelScope.launch {
            when (val result = getProductDetails(productId)) {
                is AppResult.Success -> {
                    product = result.data
                    reduce(ProductDetailsTransition.Loaded(result.data.toDetailsUi()))
                }
                is AppResult.Failure -> reduce(ProductDetailsTransition.Failed(result.error.toUiError()))
            }
        }
    }

    private fun toggle() {
        val product = product ?: return
        if (toggleJob?.isActive == true) return
        toggleJob = viewModelScope.launch {
            val isFavorite = toggleFavorite(product)
            emitEffect(ProductDetailsEffect.FavoriteToggled(isFavorite))
        }
    }
}
