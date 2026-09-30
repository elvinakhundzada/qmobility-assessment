package ae.qmobility.kmp.data.local

import androidx.room.TypeConverter
import kotlinx.serialization.json.Json

internal class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String = Json.encodeToString(value)

    @TypeConverter
    fun toStringList(value: String): List<String> = Json.decodeFromString(value)
}
