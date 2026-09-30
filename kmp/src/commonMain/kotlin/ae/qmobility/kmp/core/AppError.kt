package ae.qmobility.kmp.core

sealed interface AppError {
    data object Network : AppError

    data object NotFound : AppError

    data class Server(val code: Int) : AppError

    data class Unknown(val message: String?) : AppError
}
