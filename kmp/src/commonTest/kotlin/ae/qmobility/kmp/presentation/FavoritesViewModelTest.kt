package ae.qmobility.kmp.presentation

import ae.qmobility.kmp.domain.usecase.ObserveFavoritesUseCase
import ae.qmobility.kmp.domain.usecase.RemoveFavoriteUseCase
import ae.qmobility.kmp.fakes.FakeFavoritesRepository
import ae.qmobility.kmp.fakes.testProduct
import ae.qmobility.kmp.presentation.favorites.FavoritesEffect
import ae.qmobility.kmp.presentation.favorites.FavoritesIntent
import ae.qmobility.kmp.presentation.favorites.FavoritesViewModel
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class FavoritesViewModelTest : MainDispatcherTest() {
    private val favoritesRepository = FakeFavoritesRepository(initial = listOf(testProduct(2), testProduct(1)))

    private fun viewModel() = FavoritesViewModel(ObserveFavoritesUseCase(favoritesRepository), RemoveFavoriteUseCase(favoritesRepository))

    @Test
    fun `shows saved favorites and follows changes`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(listOf(2L, 1L), viewModel.state.value.products.map { it.id })

        favoritesRepository.add(testProduct(3))
        advanceUntilIdle()

        assertEquals(listOf(3L, 2L, 1L), viewModel.state.value.products.map { it.id })
    }

    @Test
    fun `removing the last favorite shows the empty state`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onIntent(FavoritesIntent.RemoveClicked(id = 1))
        viewModel.onIntent(FavoritesIntent.RemoveClicked(id = 2))
        advanceUntilIdle()

        assertTrue(viewModel.state.value.isEmpty)
    }

    @Test
    fun `clicking a favorite navigates to its details`() = runTest {
        val viewModel = viewModel()
        val effects = collectValues(viewModel.effects)

        viewModel.onIntent(FavoritesIntent.ProductClicked(id = 2))
        advanceUntilIdle()

        assertEquals(listOf(FavoritesEffect.NavigateToDetails(id = 2)), effects)
    }
}
