package ae.qmobility.kmp.presentation

import ae.qmobility.kmp.fakes.testProduct
import ae.qmobility.kmp.presentation.model.formatCategory
import ae.qmobility.kmp.presentation.model.formatDiscount
import ae.qmobility.kmp.presentation.model.formatOneDecimal
import ae.qmobility.kmp.presentation.model.formatPrice
import ae.qmobility.kmp.presentation.model.toDetailsUi
import ae.qmobility.kmp.presentation.model.toUi
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class ProductUiMapperTest {
    @Test
    fun `prices are formatted with two decimals`() {
        assertEquals("$9.50", formatPrice(9.5))
        assertEquals("$0.99", formatPrice(0.99))
        assertEquals("$1300.00", formatPrice(1299.999))
    }

    @Test
    fun `rating is rounded to one decimal`() {
        assertEquals("4.6", formatOneDecimal(4.56))
        assertEquals("3.0", formatOneDecimal(2.96))
    }

    @Test
    fun `discount badge is hidden when it rounds to zero`() {
        assertEquals("-13%", formatDiscount(12.96))
        assertNull(formatDiscount(0.4))
    }

    @Test
    fun `list item falls back to category when brand is missing`() {
        val ui = testProduct(id = 1, brand = null).copy(category = "mobile-accessories").toUi()

        assertEquals("Mobile accessories", ui.subtitle)
        assertEquals("Mobile accessories", formatCategory("mobile-accessories"))
    }

    @Test
    fun `details show the original price before discount`() {
        val ui = testProduct(id = 1, price = 80.0, discountPercentage = 20.0).toDetailsUi()

        assertEquals("$80.00", ui.price)
        assertEquals("$100.00", ui.originalPrice)
        assertEquals("-20%", ui.discount)
    }
}
