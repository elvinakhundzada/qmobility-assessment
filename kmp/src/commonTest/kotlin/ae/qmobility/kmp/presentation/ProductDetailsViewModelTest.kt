package ae.qmobility.kmp.presentation

import ae.qmobility.kmp.core.AppError
import ae.qmobility.kmp.domain.usecase.GetProductDetailsUseCase
import ae.qmobility.kmp.domain.usecase.ObserveIsFavoriteUseCase
import ae.qmobility.kmp.domain.usecase.ToggleFavoriteUseCase
import ae.qmobility.kmp.fakes.FakeFavoritesRepository
import ae.qmobility.kmp.fakes.FakeProductRepository
import ae.qmobility.kmp.presentation.details.ProductDetailsEffect
import ae.qmobility.kmp.presentation.details.ProductDetailsIntent
import ae.qmobility.kmp.presentation.details.ProductDetailsViewModel
import ae.qmobility.kmp.presentation.model.UiError
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductDetailsViewModelTest : MainDispatcherTest() {
    private val productRepository = FakeProductRepository()
    private val favoritesRepository = FakeFavoritesRepository()

    private fun viewModel(productId: Long = 1) = ProductDetailsViewModel(
        productId = productId,
        getProductDetails = GetProductDetailsUseCase(productRepository),
        observeIsFavorite = ObserveIsFavoriteUseCase(favoritesRepository),
        toggleFavorite = ToggleFavoriteUseCase(favoritesRepository),
    )

    @Test
    fun `loads the product and its favorite state`() = runTest {
        favoritesRepository.add(productRepository.catalog.first())

        val viewModel = viewModel(productId = 1)
        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals("Product title 1", state.product?.title)
        assertTrue(state.isFavorite)
    }

    @Test
    fun `toggling saves the favorite and confirms it`() = runTest {
        val viewModel = viewModel()
        val effects = collectValues(viewModel.effects)
        advanceUntilIdle()

        viewModel.onIntent(ProductDetailsIntent.ToggleFavorite)
        advanceUntilIdle()

        assertEquals(listOf(ProductDetailsEffect.FavoriteToggled(isFavorite = true)), effects)
        assertTrue(viewModel.state.value.isFavorite)
        assertEquals(listOf(1L), favoritesRepository.current.map { it.id })
    }

    @Test
    fun `missing product shows NotFound and retry reloads`() = runTest {
        productRepository.failWith = { AppError.NotFound }
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(UiError.NotFound, viewModel.state.value.error)

        productRepository.failWith = { null }
        viewModel.onIntent(ProductDetailsIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(1L, viewModel.state.value.product?.id)
    }

    @Test
    fun `toggle before the product loaded does nothing`() = runTest {
        productRepository.failWith = { AppError.Network }
        val viewModel = viewModel()
        advanceUntilIdle()

        viewModel.onIntent(ProductDetailsIntent.ToggleFavorite)
        advanceUntilIdle()

        assertTrue(favoritesRepository.current.isEmpty())
    }
}
