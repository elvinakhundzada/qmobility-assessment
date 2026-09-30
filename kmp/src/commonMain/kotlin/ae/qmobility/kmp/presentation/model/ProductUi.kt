package ae.qmobility.kmp.presentation.model

data class ProductUi(
    val id: Long,
    val title: String,
    val subtitle: String,
    val price: String,
    val rating: String,
    val discountBadge: String?,
    val thumbnailUrl: String,
)
