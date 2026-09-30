package ae.qmobility.kmp.fakes

import ae.qmobility.kmp.domain.model.Product

fun testProduct(
    id: Long,
    title: String = "Product title $id",
    brand: String? = "Brand $id",
    price: Double = 10.0,
    discountPercentage: Double = 0.0,
) = Product(
    id = id,
    title = title,
    description = "Description $id",
    category = "smartphones",
    brand = brand,
    price = price,
    discountPercentage = discountPercentage,
    rating = 4.5,
    stock = 5,
    availabilityStatus = "In Stock",
    thumbnail = "product_thumbnail_url",
    images = listOf("product_image_url"),
)

fun testCatalog(size: Int): List<Product> = List(size) { testProduct(id = it + 1L) }
