package ae.qmobility.kmp.data.remote

import ae.qmobility.kmp.data.remote.dto.ProductDto
import ae.qmobility.kmp.data.remote.dto.ProductsResponseDto
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

internal interface ProductRemoteDataSource {
    suspend fun getProducts(skip: Int, limit: Int): ProductsResponseDto
    suspend fun searchProducts(query: String, skip: Int, limit: Int): ProductsResponseDto
    suspend fun getProduct(id: Long): ProductDto
}

internal class KtorProductRemoteDataSource(
    private val client: HttpClient,
) : ProductRemoteDataSource {
    override suspend fun getProducts(skip: Int, limit: Int): ProductsResponseDto =
        client.get("products") {
            parameter("limit", limit)
            parameter("skip", skip)
        }.body()

    override suspend fun searchProducts(query: String, skip: Int, limit: Int): ProductsResponseDto =
        client.get("products/search") {
            parameter("q", query)
            parameter("limit", limit)
            parameter("skip", skip)
        }.body()

    override suspend fun getProduct(id: Long): ProductDto =
        client.get("products/$id").body()
}
