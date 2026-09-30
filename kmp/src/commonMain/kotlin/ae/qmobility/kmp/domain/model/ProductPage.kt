package ae.qmobility.kmp.domain.model

data class ProductPage(
    val products: List<Product>,
    val skip: Int,
    val total: Int,
) {
    val hasMore: Boolean get() = skip + products.size < total
}
