package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.repository.ProductRepository

class GetProductDetailsUseCase(private val repository: ProductRepository) {
    suspend operator fun invoke(id: Long): AppResult<Product> = repository.getProduct(id)
}
