package ae.qmobility.kmp.di

import ae.qmobility.kmp.data.local.AppDatabase
import androidx.room.Room
import androidx.room.RoomDatabase
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.Logger
import kotlinx.cinterop.ExperimentalForeignApi
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

internal actual val platformModule: Module = module {
    single<HttpClientEngine> { Darwin.create() }
    single<Logger> { Logger.DEFAULT }
    single<RoomDatabase.Builder<AppDatabase>> {
        Room.databaseBuilder<AppDatabase>(name = "${documentDirectory()}/${AppDatabase.FILE_NAME}")
    }
}

@OptIn(ExperimentalForeignApi::class)
private fun documentDirectory(): String {
    val url = NSFileManager.defaultManager.URLForDirectory(
        directory = NSDocumentDirectory,
        inDomain = NSUserDomainMask,
        appropriateForURL = null,
        create = false,
        error = null,
    )
    return requireNotNull(url?.path) { "Documents directory not found" }
}
