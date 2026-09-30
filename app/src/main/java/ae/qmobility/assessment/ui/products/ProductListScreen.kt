package ae.qmobility.assessment.ui.products

import ae.qmobility.assessment.R
import ae.qmobility.assessment.ui.components.CollectEffects
import ae.qmobility.assessment.ui.components.ErrorRow
import ae.qmobility.assessment.ui.components.FullScreenError
import ae.qmobility.assessment.ui.components.FullScreenLoading
import ae.qmobility.assessment.ui.components.FullScreenMessage
import ae.qmobility.assessment.ui.components.GradientHeader
import ae.qmobility.assessment.ui.components.LoadingRow
import ae.qmobility.assessment.ui.components.ProductCard
import ae.qmobility.assessment.ui.components.SearchField
import ae.qmobility.assessment.ui.theme.QMobilityTheme
import ae.qmobility.kmp.presentation.model.ProductUi
import ae.qmobility.kmp.presentation.model.UiError
import ae.qmobility.kmp.presentation.products.ProductListEffect
import ae.qmobility.kmp.presentation.products.ProductListIntent
import ae.qmobility.kmp.presentation.products.ProductListState
import ae.qmobility.kmp.presentation.products.ProductListViewModel
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import org.koin.compose.viewmodel.koinViewModel

private const val PREFETCH_DISTANCE = 5

@Composable
fun ProductListScreen(
    onOpenDetails: (Long) -> Unit,
    viewModel: ProductListViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is ProductListEffect.NavigateToDetails -> onOpenDetails(effect.id)
        }
    }

    ProductListContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun ProductListContent(state: ProductListState, onIntent: (ProductListIntent) -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        GradientHeader(
            title = stringResource(R.string.app_name),
        ) {
            Spacer(Modifier.height(12.dp))
            SearchField(query = state.query, onQueryChange = { onIntent(ProductListIntent.QueryChanged(it)) })
        }
        val error = state.error
        when {
            state.isLoading -> FullScreenLoading()
            error != null -> FullScreenError(error, onRetry = { onIntent(ProductListIntent.Retry) })
            state.isEmpty -> FullScreenMessage(
                title = if (state.activeQuery.isBlank()) {
                    stringResource(R.string.products_empty)
                } else {
                    stringResource(R.string.products_empty_query, state.activeQuery)
                },
            )
            else -> ProductList(state, onIntent)
        }
    }
}

@Composable
private fun ProductList(state: ProductListState, onIntent: (ProductListIntent) -> Unit) {
    val listState = rememberLazyListState()
    LaunchedEffect(state.activeQuery) { listState.scrollToItem(0) }
    LoadMoreWhenIfNeeded(listState, onLoadMore = { onIntent(ProductListIntent.LoadNextPage) })

    LazyColumn(
        state = listState,
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        items(state.products, key = { it.id }) { product ->
            ProductCard(product = product, onProductCardClicked = { onIntent(ProductListIntent.ProductClicked(product.id)) })
        }
        val loadMoreError = state.loadMoreError
        when {
            state.isLoadingMore -> item(key = "loading") { LoadingRow() }
            loadMoreError != null -> item(key = "error") {
                ErrorRow(loadMoreError, onRetry = { onIntent(ProductListIntent.Retry) })
            }
        }
    }
}

@Composable
private fun LoadMoreWhenIfNeeded(listState: LazyListState, onLoadMore: () -> Unit) {
    val currentOnLoadMore by rememberUpdatedState(onLoadMore)
    LaunchedEffect(listState) {
        snapshotFlow {
            val layout = listState.layoutInfo
            val lastVisible = layout.visibleItemsInfo.lastOrNull()?.index ?: return@snapshotFlow false
            layout.totalItemsCount > 0 && lastVisible >= layout.totalItemsCount - 1 - PREFETCH_DISTANCE
        }
            .distinctUntilChanged()
            .filter { it }
            .collect { currentOnLoadMore() }
    }
}

private val previewProducts = List(4) {
    ProductUi(
        id = it.toLong(),
        title = "Essence Mascara Lash Princess",
        subtitle = "Essence",
        price = "$9.99",
        rating = "4.9",
        discountBadge = "-7%",
        thumbnailUrl = "",
    )
}

@Preview(showBackground = true)
@Composable
private fun ProductListPreview() {
    QMobilityTheme {
        ProductListContent(
            state = ProductListState(products = previewProducts, isLoading = false, isLoadingMore = true),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductListErrorPreview() {
    QMobilityTheme {
        ProductListContent(state = ProductListState(isLoading = false, error = UiError.NoConnection), onIntent = {})
    }
}
