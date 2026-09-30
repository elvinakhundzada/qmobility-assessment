package ae.qmobility.kmp.di

import ae.qmobility.kmp.data.local.AppDatabase
import ae.qmobility.kmp.data.local.buildDatabase
import ae.qmobility.kmp.data.remote.KtorProductRemoteDataSource
import ae.qmobility.kmp.data.remote.ProductRemoteDataSource
import ae.qmobility.kmp.data.remote.createHttpClient
import ae.qmobility.kmp.data.repository.FavoritesRepositoryImpl
import ae.qmobility.kmp.data.repository.ProductRepositoryImpl
import ae.qmobility.kmp.domain.repository.FavoritesRepository
import ae.qmobility.kmp.domain.repository.ProductRepository
import ae.qmobility.kmp.domain.usecase.GetProductDetailsUseCase
import ae.qmobility.kmp.domain.usecase.GetProductPageUseCase
import ae.qmobility.kmp.domain.usecase.ObserveFavoritesUseCase
import ae.qmobility.kmp.domain.usecase.ObserveIsFavoriteUseCase
import ae.qmobility.kmp.domain.usecase.RemoveFavoriteUseCase
import ae.qmobility.kmp.domain.usecase.ToggleFavoriteUseCase
import ae.qmobility.kmp.presentation.details.ProductDetailsViewModel
import ae.qmobility.kmp.presentation.favorites.FavoritesViewModel
import ae.qmobility.kmp.presentation.products.ProductListViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

internal expect val platformModule: Module

internal val dataModule = module {
    single {
        createHttpClient(
            engine = get(),
            logger = if (get<CommonConfiguration>().enableNetworkLogs) get() else null,
        )
    }
    single<ProductRemoteDataSource> { KtorProductRemoteDataSource(client = get()) }
    single { buildDatabase(builder = get()) }
    single { get<AppDatabase>().favoriteProductDao() }
    single<ProductRepository> { ProductRepositoryImpl(remote = get()) }
    single<FavoritesRepository> { FavoritesRepositoryImpl(dao = get()) }
}

internal val domainModule = module {
    factory { GetProductPageUseCase(repository = get()) }
    factoryOf(::GetProductDetailsUseCase)
    factoryOf(::ObserveFavoritesUseCase)
    factoryOf(::ObserveIsFavoriteUseCase)
    factoryOf(::ToggleFavoriteUseCase)
    factoryOf(::RemoveFavoriteUseCase)
}

internal val presentationModule = module {
    viewModelOf(::ProductListViewModel)
    viewModel { params ->
        ProductDetailsViewModel(
            productId = params.get(),
            getProductDetails = get(),
            observeIsFavorite = get(),
            toggleFavorite = get(),
        )
    }
    viewModelOf(::FavoritesViewModel)
}

internal fun sharedModules(config: CommonConfiguration): List<Module> =
    listOf(module { single { config } }, platformModule, dataModule, domainModule, presentationModule)
