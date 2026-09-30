package ae.qmobility.kmp.data.repository

import ae.qmobility.kmp.core.AppError
import ae.qmobility.kmp.core.AppResult
import io.ktor.client.network.sockets.ConnectTimeoutException
import io.ktor.client.plugins.HttpRequestTimeoutException
import io.ktor.client.plugins.ResponseException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException
import kotlinx.io.IOException

internal inline fun <T> safeApiCall(block: () -> T): AppResult<T> =
    try {
        AppResult.Success(block())
    } catch (e: CancellationException) {
        throw e
    } catch (e: ResponseException) {
        val status = e.response.status
        AppResult.Failure(
            if (status == HttpStatusCode.NotFound) AppError.NotFound else AppError.Server(status.value),
        )
    } catch (e: HttpRequestTimeoutException) {
        AppResult.Failure(AppError.Network)
    } catch (e: ConnectTimeoutException) {
        AppResult.Failure(AppError.Network)
    } catch (e: IOException) {
        AppResult.Failure(AppError.Network)
    } catch (e: Exception) {
        AppResult.Failure(AppError.Unknown(e.message))
    }
