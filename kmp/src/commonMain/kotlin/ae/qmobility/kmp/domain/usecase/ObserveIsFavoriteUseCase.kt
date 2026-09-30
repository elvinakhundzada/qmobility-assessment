package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow

class ObserveIsFavoriteUseCase(private val repository: FavoritesRepository) {
    operator fun invoke(id: Long): Flow<Boolean> = repository.observeFavoritesByProductId(id)
}
