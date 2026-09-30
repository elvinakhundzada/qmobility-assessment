package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.repository.FavoritesRepository

class ToggleFavoriteUseCase(private val repository: FavoritesRepository) {
    suspend operator fun invoke(product: Product): Boolean =
        if (repository.isFavorite(product.id)) {
            repository.remove(product.id)
            false
        } else {
            repository.add(product)
            true
        }
}
