package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.domain.repository.FavoritesRepository

class RemoveFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(id: Long) = repository.remove(id)
}
