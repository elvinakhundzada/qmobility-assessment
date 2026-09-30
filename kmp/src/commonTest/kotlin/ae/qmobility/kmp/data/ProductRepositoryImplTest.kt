package ae.qmobility.kmp.data

import ae.qmobility.kmp.core.AppError
import ae.qmobility.kmp.core.AppResult
import ae.qmobility.kmp.data.remote.KtorProductRemoteDataSource
import ae.qmobility.kmp.data.remote.createHttpClient
import ae.qmobility.kmp.data.repository.ProductRepositoryImpl
import ae.qmobility.kmp.domain.model.ProductPage
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.MockRequestHandler
import io.ktor.client.engine.mock.respond
import io.ktor.client.engine.mock.respondError
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import kotlinx.io.IOException
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

class ProductRepositoryImplTest {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")

    private fun repository(handler: MockRequestHandler) =
        ProductRepositoryImpl(KtorProductRemoteDataSource(createHttpClient(MockEngine(handler))))

    @Test
    fun `search sends query and paging parameters and parses the page`() = runTest {
        var requestedUrl = ""
        val repository = repository { request ->
            requestedUrl = request.url.toString()
            respond(
                content = """
                    {"products":[{"id":1,"title":"iPhone","price":9.5,"brand":"Apple","unknownField":true}],
                     "total":41,"skip":20,"limit":20}
                """.trimIndent(),
                headers = jsonHeaders,
            )
        }

        val result = repository.searchProducts(query = "phone", skip = 20, limit = 20)

        assertEquals("https://dummyjson.com/products/search?q=phone&limit=20&skip=20", requestedUrl)
        val page = assertIs<AppResult.Success<ProductPage>>(result).data
        assertEquals(41, page.total)
        assertEquals("iPhone", page.products.single().title)
    }

    @Test
    fun `404 maps to NotFound`() = runTest {
        val repository = repository { respondError(HttpStatusCode.NotFound) }

        assertEquals(AppResult.Failure(AppError.NotFound), repository.getProduct(id = 999))
    }

    @Test
    fun `other http errors map to Server with the status code`() = runTest {
        val repository = repository { respondError(HttpStatusCode.ServiceUnavailable) }

        assertEquals(AppResult.Failure(AppError.Server(503)), repository.getProducts(skip = 0, limit = 20))
    }

    @Test
    fun `io failure maps to Network`() = runTest {
        val repository = repository { throw IOException("offline") }

        assertEquals(AppResult.Failure(AppError.Network), repository.getProducts(skip = 0, limit = 20))
    }

    @Test
    fun `incorrect response body maps to Unknown`() = runTest {
        val repository = repository { respond(content = "{}}", headers = jsonHeaders) }

        assertIs<AppError.Unknown>(assertIs<AppResult.Failure>(repository.getProduct(id = 1)).error)
    }
}
