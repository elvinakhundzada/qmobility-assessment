package ae.qmobility.kmp.domain.repository

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.model.ProductPage

interface ProductRepository {
    suspend fun getProducts(skip: Int, limit: Int): AppResult<ProductPage>
    suspend fun searchProducts(query: String, skip: Int, limit: Int): AppResult<ProductPage>
    suspend fun getProduct(id: Long): AppResult<Product>
}
