package ae.qmobility.kmp.domain

import ae.qmobility.kmp.domain.usecase.ToggleFavoriteUseCase
import ae.qmobility.kmp.fakes.FakeFavoritesRepository
import ae.qmobility.kmp.fakes.testProduct
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ToggleFavoriteUseCaseTest {
    private val product = testProduct(id = 1)

    @Test
    fun `saves a product that is not a favorite`() = runTest {
        val repository = FakeFavoritesRepository()

        val isFavorite = ToggleFavoriteUseCase(repository)(product)

        assertTrue(isFavorite)
        assertEquals(listOf(product), repository.current)
    }

    @Test
    fun `removes a product that is already a favorite`() = runTest {
        val repository = FakeFavoritesRepository(initial = listOf(product))

        val isFavorite = ToggleFavoriteUseCase(repository)(product)

        assertFalse(isFavorite)
        assertTrue(repository.current.isEmpty())
    }
}
