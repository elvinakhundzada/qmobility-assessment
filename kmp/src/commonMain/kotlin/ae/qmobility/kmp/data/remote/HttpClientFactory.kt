package ae.qmobility.kmp.data.remote

import io.ktor.client.HttpClient
import io.ktor.client.engine.HttpClientEngine
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

internal const val BASE_URL = "https://dummyjson.com/"

internal fun createHttpClient(engine: HttpClientEngine, logger: Logger? = null): HttpClient = HttpClient(engine) {
    expectSuccess = true

    install(ContentNegotiation) {
        json(Json {
            ignoreUnknownKeys = true
            prettyPrint = true
        })
    }
    install(HttpTimeout) {
        requestTimeoutMillis = 15000L
        connectTimeoutMillis = 10000L
    }
    defaultRequest { url(BASE_URL) }

    if (logger != null) {
        install(Logging) {
            this.logger = logger
            level = LogLevel.ALL
        }
    }
}
