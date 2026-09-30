package ae.qmobility.kmp.domain.repository

import ae.qmobility.kmp.domain.model.Product
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    fun observeFavorites(): Flow<List<Product>>
    fun observeFavoritesByProductId(productId: Long): Flow<Boolean>
    suspend fun isFavorite(productId: Long): Boolean
    suspend fun add(product: Product)
    suspend fun remove(productId: Long)
}
