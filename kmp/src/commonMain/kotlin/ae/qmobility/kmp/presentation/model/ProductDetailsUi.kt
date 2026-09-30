package ae.qmobility.kmp.presentation.model

data class ProductDetailsUi(
    val id: Long,
    val title: String,
    val description: String,
    val brand: String?,
    val category: String,
    val price: String,
    val originalPrice: String?,
    val discount: String?,
    val rating: String,
    val stock: Int,
    val availability: String?,
    val images: List<String>,
)
