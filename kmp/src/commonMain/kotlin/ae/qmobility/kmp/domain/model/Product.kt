package ae.qmobility.kmp.domain.model

data class Product(
    val id: Long,
    val title: String,
    val description: String,
    val category: String,
    val brand: String?,
    val price: Double,
    val discountPercentage: Double,
    val rating: Double,
    val stock: Int,
    val availabilityStatus: String?,
    val thumbnail: String,
    val images: List<String>,
)
