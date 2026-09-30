package ae.qmobility.kmp.data.repository

import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.data.mapper.toDomain
import ae.qmobility.kmp.data.remote.ProductRemoteDataSource
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.model.ProductPage
import ae.qmobility.kmp.domain.repository.ProductRepository

internal class ProductRepositoryImpl(
    private val remote: ProductRemoteDataSource,
) : ProductRepository {
    override suspend fun getProducts(skip: Int, limit: Int): AppResult<ProductPage> =
        safeApiCall { remote.getProducts(skip, limit).toDomain() }

    override suspend fun searchProducts(query: String, skip: Int, limit: Int): AppResult<ProductPage> =
        safeApiCall { remote.searchProducts(query, skip, limit).toDomain() }

    override suspend fun getProduct(id: Long): AppResult<Product> =
        safeApiCall { remote.getProduct(id).toDomain() }
}
