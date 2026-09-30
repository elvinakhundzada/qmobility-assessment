package ae.qmobility.kmp.fakes

import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeFavoritesRepository(initial: List<Product> = emptyList()) : FavoritesRepository {
    private val favorites = MutableStateFlow(initial)

    val current: List<Product> get() = favorites.value

    override fun observeFavorites(): Flow<List<Product>> = favorites

    override fun observeFavoritesByProductId(productId: Long): Flow<Boolean> =
        favorites.map { list -> list.any { it.id == productId } }.distinctUntilChanged()

    override suspend fun isFavorite(productId: Long): Boolean = favorites.value.any { it.id == productId }

    override suspend fun add(product: Product) =
        favorites.update { list -> listOf(product) + list.filterNot { it.id == product.id } }

    override suspend fun remove(productId: Long) =
        favorites.update { list -> list.filterNot { it.id == productId } }
}
