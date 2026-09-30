package ae.qmobility.kmp.domain.usecase

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.model.ProductPage
import ae.qmobility.kmp.domain.repository.ProductRepository

class GetProductPageUseCase(
    private val repository: ProductRepository,
    private val pageSize: Int = DEFAULT_PAGE_SIZE,
) {
    suspend operator fun invoke(query: String, skip: Int): AppResult<ProductPage> {
        val trimmed = query.trim()
        return if (trimmed.isEmpty()) {
            repository.getProducts(skip = skip, limit = pageSize)
        } else {
            repository.searchProducts(query = trimmed, skip = skip, limit = pageSize)
        }
    }

    companion object {
        const val DEFAULT_PAGE_SIZE = 20
    }
}
