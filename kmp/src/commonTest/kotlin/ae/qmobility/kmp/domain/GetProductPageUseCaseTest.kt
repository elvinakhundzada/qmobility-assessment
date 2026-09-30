package ae.qmobility.kmp.domain

import ae.qmobility.kmp.domain.usecase.GetProductPageUseCase
import ae.qmobility.kmp.fakes.FakeProductRepository
import ae.qmobility.kmp.fakes.FakeProductRepository.Request
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class GetProductPageUseCaseTest {
    private val repository = FakeProductRepository()
    private val getProductPage = GetProductPageUseCase(repository, pageSize = 10)

    @Test
    fun `blank query loads the plain product list`() = runTest {
        getProductPage(query = "   ", skip = 30)

        assertEquals(listOf(Request(query = null, skip = 30, limit = 10)), repository.requests)
    }

    @Test
    fun `non blank query searches with the trimmed query`() = runTest {
        getProductPage(query = "  phone ", skip = 0)

        assertEquals(listOf(Request(query = "phone", skip = 0, limit = 10)), repository.requests)
    }
}
