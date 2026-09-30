package ae.qmobility.kmp.data

import ae.qmobility.kmp.data.local.FavoriteProductEntity
import ae.qmobility.kmp.data.mapper.toDomain
import ae.qmobility.kmp.data.mapper.toEntity
import ae.qmobility.kmp.data.remote.dto.ProductDto
import ae.qmobility.kmp.data.remote.dto.ProductsResponseDto
import ae.qmobility.kmp.domain.model.Product
import ae.qmobility.kmp.fakes.testProduct
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class ProductMappersTest {
    @Test
    fun `dto maps to domain product`() {
        val dto = ProductDto(
            id = 1,
            title = "iPhone 9",
            description = "An apple mobile",
            category = "smartphones",
            brand = "Apple",
            price = 549.0,
            discountPercentage = 12.96,
            rating = 4.69,
            stock = 94,
            availabilityStatus = "In Stock",
            thumbnail = "thumb.jpg",
            images = listOf("1.jpg", "2.jpg"),
        )

        val product = dto.toDomain()

        assertEquals(1, product.id)
        assertEquals("iPhone 9", product.title)
        assertEquals("Apple", product.brand)
        assertEquals(549.0, product.price)
        assertEquals(listOf("1.jpg", "2.jpg"), product.images)
    }

    @Test
    fun `empty brand maps to null`() {
        assertNull(ProductDto(id = 1, title = "Soap", brand = " ").toDomain().brand)
    }

    @Test
    fun `response maps to page and knows whether more pages exist`() {
        val response = ProductsResponseDto(
            products = List(20) { ProductDto(id = it.toLong(), title = "P$it") },
            total = 50,
            skip = 20,
            limit = 20,
        )

        val page = response.toDomain()

        assertEquals(20, page.skip)
        assertEquals(50, page.total)
        assertTrue(page.hasMore)
        assertFalse(response.copy(skip = 40, products = response.products.take(10)).toDomain().hasMore)
    }

    @Test
    fun `product maps to entity with saved timestamp`() {
        val product = testProduct(id = 7, discountPercentage = 5.0)

        val entity = product.toEntity(savedAtEpochMillis = 1_000)

        assertEquals(
            FavoriteProductEntity(
                id = 7,
                title = product.title,
                description = product.description,
                category = product.category,
                brand = product.brand,
                price = product.price,
                discountPercentage = 5.0,
                rating = product.rating,
                stock = product.stock,
                availabilityStatus = product.availabilityStatus,
                thumbnail = product.thumbnail,
                images = product.images,
                savedAtEpochMillis = 1_000,
            ),
            entity,
        )
    }

    @Test
    fun `entity maps to domain product`() {
        val entity = FavoriteProductEntity(
            id = 3,
            title = "Apple AirPods Max",
            description = "Over-ear headphones",
            category = "mobile-accessories",
            brand = null,
            price = 549.99,
            discountPercentage = 13.67,
            rating = 3.5,
            stock = 59,
            availabilityStatus = "In Stock",
            thumbnail = "thumb.webp",
            images = listOf("1.webp", "2.webp"),
            savedAtEpochMillis = 1_000,
        )

        val product = entity.toDomain()

        assertEquals(
            Product(
                id = 3,
                title = "Apple AirPods Max",
                description = "Over-ear headphones",
                category = "mobile-accessories",
                brand = null,
                price = 549.99,
                discountPercentage = 13.67,
                rating = 3.5,
                stock = 59,
                availabilityStatus = "In Stock",
                thumbnail = "thumb.webp",
                images = listOf("1.webp", "2.webp"),
            ),
            product,
        )
    }
}
