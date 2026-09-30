package ae.qmobility.kmp.data.repository

import ae.qmobility.kmp.data.local.FavoriteProductDao
import ae.qmobility.kmp.data.mapper.toDomain
import ae.qmobility.kmp.data.mapper.toEntity
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlin.time.Clock

internal class FavoritesRepositoryImpl(
    private val dao: FavoriteProductDao,
    private val clock: Clock = Clock.System,
) : FavoritesRepository {
    override fun observeFavorites(): Flow<List<Product>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeFavoritesByProductId(productId: Long): Flow<Boolean> = dao.observeFavoritesByProductId(productId)

    override suspend fun isFavorite(productId: Long): Boolean = dao.isFavoriteByProductId(productId)

    override suspend fun add(product: Product) =
        dao.insert(product.toEntity(savedAtEpochMillis = clock.now().toEpochMilliseconds()))

    override suspend fun remove(productId: Long) = dao.deleteById(productId)
}
