package ae.qmobility.kmp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
internal interface FavoriteProductDao {
    @Query("SELECT * FROM favorite_products ORDER BY savedAtEpochMillis DESC")
    fun observeAll(): Flow<List<FavoriteProductEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :id)")
    fun observeFavoritesByProductId(id: Long): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_products WHERE id = :id)")
    suspend fun isFavoriteByProductId(id: Long): Boolean

    @Upsert
    suspend fun insert(entity: FavoriteProductEntity)

    @Query("DELETE FROM favorite_products WHERE id = :id")
    suspend fun deleteById(id: Long)
}
