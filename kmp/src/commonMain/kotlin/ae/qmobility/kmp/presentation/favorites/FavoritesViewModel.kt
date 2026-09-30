package ae.qmobility.kmp.presentation.favorites

import ae.qmobility.kmp.domain.usecase.ObserveFavoritesUseCase
import ae.qmobility.kmp.domain.usecase.RemoveFavoriteUseCase
import ae.qmobility.kmp.presentation.model.toUi
import ae.qmobility.kmp.presentation.mvi.MviViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class FavoritesViewModel(
    observeFavorites: ObserveFavoritesUseCase,
    private val removeFavorite: RemoveFavoriteUseCase,
) : MviViewModel<FavoritesIntent, FavoritesState, FavoritesTransition, FavoritesEffect>(
    initialState = FavoritesState(),
    reducer = FavoritesReducer,
) {
    init {
        observeFavorites()
            .onEach { products -> reduce(FavoritesTransition.Loaded(products.map { it.toUi() })) }
            .launchIn(viewModelScope)
    }

    override fun onIntent(intent: FavoritesIntent) {
        when (intent) {
            is FavoritesIntent.ProductClicked -> emitEffect(FavoritesEffect.NavigateToDetails(intent.id))
            is FavoritesIntent.RemoveClicked -> viewModelScope.launch { removeFavorite(intent.id) }
        }
    }
}
