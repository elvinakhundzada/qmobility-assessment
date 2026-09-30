package ae.qmobility.kmp.data.remote.dto

import kotlinx.serialization.Serializable

@Serializable
internal data class ProductDto(
    val id: Long,
    val title: String,
    val description: String = "",
    val category: String = "",
    val brand: String? = null,
    val price: Double = 0.0,
    val discountPercentage: Double = 0.0,
    val rating: Double = 0.0,
    val stock: Int = 0,
    val availabilityStatus: String? = null,
    val thumbnail: String = "",
    val images: List<String> = emptyList(),
)
