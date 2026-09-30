package ae.qmobility.kmp.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "favorite_products")
internal data class FavoriteProductEntity(
    @PrimaryKey val id: Long,
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
    val savedAtEpochMillis: Long,
)
