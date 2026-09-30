package ae.qmobility.kmp.fakes

import ae.qmobility.kmp.core.AppError
import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.model.ProductPage
import ae.qmobility.kmp.domain.repository.ProductRepository

class FakeProductRepository(
    var catalog: List<Product> = testCatalog(size = 50),
) : ProductRepository {
    data class Request(val query: String?, val skip: Int, val limit: Int)

    val requests = mutableListOf<Request>()

    var failWith: (Request) -> AppError? = { null }

    override suspend fun getProducts(skip: Int, limit: Int) = page(Request(null, skip, limit))

    override suspend fun searchProducts(query: String, skip: Int, limit: Int) = page(Request(query, skip, limit))

    override suspend fun getProduct(id: Long): AppResult<Product> {
        failWith(Request(query = "id:$id", skip = 0, limit = 1))?.let { return AppResult.Failure(it) }
        return catalog.firstOrNull { it.id == id }
            ?.let { AppResult.Success(it) }
            ?: AppResult.Failure(AppError.NotFound)
    }

    private fun page(request: Request): AppResult<ProductPage> {
        requests += request
        failWith(request)?.let { return AppResult.Failure(it) }
        val matching = request.query
            ?.let { query -> catalog.filter { it.title.contains(query, ignoreCase = true) } }
            ?: catalog
        return AppResult.Success(
            ProductPage(
                products = matching.drop(request.skip).take(request.limit),
                skip = request.skip,
                total = matching.size,
            ),
        )
    }
}
