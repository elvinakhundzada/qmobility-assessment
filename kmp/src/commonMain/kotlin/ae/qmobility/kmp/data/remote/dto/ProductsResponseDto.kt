package ae.qmobility.kmp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ProductsResponseDto(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int,
)
