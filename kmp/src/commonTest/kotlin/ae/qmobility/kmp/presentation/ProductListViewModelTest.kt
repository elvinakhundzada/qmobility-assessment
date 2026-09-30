package ae.qmobility.kmp.presentation

import ae.qmobility.kmp.core.AppError
import ae.qmobility.kmp.domain.usecase.GetProductPageUseCase
import ae.qmobility.kmp.fakes.FakeProductRepository
import ae.qmobility.kmp.fakes.FakeProductRepository.Request
import ae.qmobility.kmp.fakes.testCatalog
import ae.qmobility.kmp.fakes.testProduct
import ae.qmobility.kmp.presentation.model.UiError
import ae.qmobility.kmp.presentation.products.ProductListEffect
import ae.qmobility.kmp.presentation.products.ProductListIntent
import ae.qmobility.kmp.presentation.products.ProductListViewModel
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductListViewModelTest : MainDispatcherTest() {
    private val repository = FakeProductRepository(catalog = testCatalog(size = 25))

    private fun viewModel() = ProductListViewModel(GetProductPageUseCase(repository, pageSize = 10))

    @Test
    fun `loads the first page on start`() = runTest {
        val viewModel = viewModel()
        assertTrue(viewModel.state.value.isLoading)

        advanceUntilIdle()

        val state = viewModel.state.value
        assertFalse(state.isLoading)
        assertEquals((1L..10L).toList(), state.products.map { it.id })
        assertEquals(10, state.nextOffset)
        assertFalse(state.endReached)
    }

    @Test
    fun `pages until the end and then stops requesting`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()

        repeat(3) {
            viewModel.onIntent(ProductListIntent.LoadNextPage)
            advanceUntilIdle()
        }

        val state = viewModel.state.value
        assertEquals(25, state.products.size)
        assertTrue(state.endReached)
        assertEquals(listOf(0, 10, 20), repository.requests.map { it.skip })
    }

    @Test
    fun `search waits for typing to pause and restarts paging`() = runTest {
        repository.catalog = testCatalog(size = 25) + testProduct(id = 100, title = "Phone case")
        val viewModel = viewModel()
        advanceUntilIdle()
        viewModel.onIntent(ProductListIntent.LoadNextPage)
        advanceUntilIdle()
        repository.requests.clear()

        viewModel.onIntent(ProductListIntent.QueryChanged("p"))
        advanceTimeBy(100)
        viewModel.onIntent(ProductListIntent.QueryChanged("ph"))
        advanceTimeBy(100)
        viewModel.onIntent(ProductListIntent.QueryChanged("phone"))
        assertEquals("phone", viewModel.state.value.query)
        advanceUntilIdle()

        assertEquals(listOf(Request(query = "phone", skip = 0, limit = 10)), repository.requests)
        assertEquals(listOf(100L), viewModel.state.value.products.map { it.id })
        assertTrue(viewModel.state.value.endReached)
    }

    @Test
    fun `first page failure shows an error and retry recovers`() = runTest {
        repository.failWith = { AppError.Network }
        val viewModel = viewModel()
        advanceUntilIdle()
        assertEquals(UiError.NoConnection, viewModel.state.value.error)

        repository.failWith = { null }
        viewModel.onIntent(ProductListIntent.Retry)
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(10, viewModel.state.value.products.size)
    }

    @Test
    fun `next page failure keeps loaded products and waits for retry`() = runTest {
        val viewModel = viewModel()
        advanceUntilIdle()
        repository.failWith = { request -> if (request.skip > 0) AppError.Server(500) else null }

        viewModel.onIntent(ProductListIntent.LoadNextPage)
        advanceUntilIdle()
        assertEquals(UiError.Generic, viewModel.state.value.loadMoreError)
        assertEquals(10, viewModel.state.value.products.size)

        viewModel.onIntent(ProductListIntent.LoadNextPage)
        advanceUntilIdle()
        assertEquals(2, repository.requests.size)

        repository.failWith = { null }
        viewModel.onIntent(ProductListIntent.Retry)
        advanceUntilIdle()
        assertNull(viewModel.state.value.loadMoreError)
        assertEquals(20, viewModel.state.value.products.size)
    }

    @Test
    fun `clicking a product navigates to its details`() = runTest {
        val viewModel = viewModel()
        val effects = collectValues(viewModel.effects)

        viewModel.onIntent(ProductListIntent.ProductClicked(id = 3))
        advanceUntilIdle()

        assertEquals(listOf(ProductListEffect.NavigateToDetails(id = 3)), effects)
    }
}
