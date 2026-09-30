package ae.qmobility.kmp.data.mapper

import ae.qmobility.kmp.data.local.FavoriteProductEntity
import ae.qmobility.kmp.data.remote.dto.ProductDto
import ae.qmobility.kmp.data.remote.dto.ProductsResponseDto
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.domain.model.ProductPage

internal fun ProductDto.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    brand = brand?.takeIf { it.isNotBlank() },
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    availabilityStatus = availabilityStatus,
    thumbnail = thumbnail,
    images = images,
)

internal fun ProductsResponseDto.toDomain(): ProductPage = ProductPage(
    products = products.map { it.toDomain() },
    skip = skip,
    total = total,
)

internal fun Product.toEntity(savedAtEpochMillis: Long): FavoriteProductEntity = FavoriteProductEntity(
    id = id,
    title = title,
    description = description,
    category = category,
    brand = brand,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    availabilityStatus = availabilityStatus,
    thumbnail = thumbnail,
    images = images,
    savedAtEpochMillis = savedAtEpochMillis,
)

internal fun FavoriteProductEntity.toDomain(): Product = Product(
    id = id,
    title = title,
    description = description,
    category = category,
    brand = brand,
    price = price,
    discountPercentage = discountPercentage,
    rating = rating,
    stock = stock,
    availabilityStatus = availabilityStatus,
    thumbnail = thumbnail,
    images = images,
)
