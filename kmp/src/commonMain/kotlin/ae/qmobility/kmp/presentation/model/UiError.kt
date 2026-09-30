package ae.qmobility.kmp.presentation.model

import ae.qmobility.kmp.core.AppError

enum class UiError { NoConnection, NotFound, Generic }

internal fun AppError.toUiError(): UiError = when (this) {
    AppError.Network -> UiError.NoConnection
    AppError.NotFound -> UiError.NotFound
    is AppError.Server, is AppError.Unknown -> UiError.Generic
}
