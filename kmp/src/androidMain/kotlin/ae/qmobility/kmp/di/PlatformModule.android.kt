package ae.qmobility.kmp.di

import ae.qmobility.kmp.data.local.AppDatabase
import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.okhttp.OkHttp
import io.ktor.client.plugins.logging.ANDROID
import io.ktor.client.plugins.logging.Logger
import org.koin.core.module.Module
import org.koin.dsl.module

internal actual val platformModule: Module = module {
    single<HttpClientEngine> { OkHttp.create() }
    single<Logger> { Logger.ANDROID }
    single<RoomDatabase.Builder<AppDatabase>> {
        val context = get<Context>().applicationContext
        Room.databaseBuilder<AppDatabase>(
            context = context,
            name = context.getDatabasePath(AppDatabase.FILE_NAME).absolutePath,
        )
    }
}
