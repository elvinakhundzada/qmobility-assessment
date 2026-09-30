package ae.qmobility.kmp.presentation.model

import ae.qmobility.kmp.domain.model.Product
import kotlin.math.abs
import kotlin.math.roundToLong

internal fun Product.toUi(): ProductUi = ProductUi(
    id = id,
    title = title,
    subtitle = brand ?: formatCategory(category),
    price = formatPrice(price),
    rating = formatOneDecimal(rating),
    discountBadge = formatDiscount(discountPercentage),
    thumbnailUrl = thumbnail,
)

internal fun Product.toDetailsUi(): ProductDetailsUi = ProductDetailsUi(
    id = id,
    title = title,
    description = description,
    brand = brand,
    category = formatCategory(category),
    price = formatPrice(price),
    originalPrice = originalPrice(price, discountPercentage)?.let(::formatPrice),
    discount = formatDiscount(discountPercentage),
    rating = formatOneDecimal(rating),
    stock = stock,
    availability = availabilityStatus,
    images = images.ifEmpty { listOfNotNull(thumbnail.takeIf { it.isNotBlank() }) },
)

internal fun formatPrice(value: Double): String {
    val cents = (value * 100).roundToLong()
    return "$" + (cents / 100) + "." + abs(cents % 100).toString().padStart(2, '0')
}

internal fun formatOneDecimal(value: Double): String {
    val tenths = (value * 10).roundToLong()
    return "" + (tenths / 10) + "." + abs(tenths % 10)
}

internal fun formatDiscount(percentage: Double): String? {
    val rounded = percentage.roundToLong()
    return if (rounded > 0) "-$rounded%" else null
}

internal fun formatCategory(category: String): String =
    category.replace('-', ' ').replaceFirstChar { it.uppercaseChar() }

private fun originalPrice(price: Double, discountPercentage: Double): Double? =
    if (discountPercentage > 0 && discountPercentage < 100) price / (1 - discountPercentage / 100) else null
