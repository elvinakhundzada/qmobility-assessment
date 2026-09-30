package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class ObserveFavoritesUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(): Flow<List<Product>> = repository.observeFavorites()
}
