package ae.qmobility.kmp.presentation

import ae.qmobility.kmp.fakes.testProduct
import ae.qmobility.kmp.presentation.model.UiError
import ae.qmobility.kmp.presentation.model.toUi
import ae.qmobility.kmp.presentation.products.ProductListTransition
import ae.qmobility.kmp.presentation.products.ProductListReducer
import ae.qmobility.kmp.presentation.products.ProductListState
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductListReducerTest {
    private val loaded = ProductListState(
        query = "old",
        activeQuery = "old",
        products = listOf(testProduct(1).toUi(), testProduct(2).toUi()),
        nextOffset = 2,
        endReached = true,
        isLoading = false,
        loadMoreError = UiError.NoConnection,
    )

    @Test
    fun `first page loading clears the previous query's results`() {
        val state = ProductListReducer.reduce(loaded, ProductListTransition.FirstPageLoading(query = "new"))

        assertEquals("new", state.activeQuery)
        assertTrue(state.products.isEmpty())
        assertEquals(0, state.nextOffset)
        assertTrue(state.isLoading)
        assertEquals(false, state.endReached)
        assertNull(state.loadMoreError)
    }

    @Test
    fun `next page appends without duplicating products`() {
        val nextPage = listOf(testProduct(2).toUi(), testProduct(3).toUi())

        val state = ProductListReducer.reduce(
            loaded.copy(isLoadingMore = true),
            ProductListTransition.NextPageLoaded(products = nextPage, nextSkip = 4, endReached = false),
        )

        assertEquals(listOf(1L, 2L, 3L), state.products.map { it.id })
        assertEquals(4, state.nextOffset)
        assertEquals(false, state.isLoadingMore)
    }
}
