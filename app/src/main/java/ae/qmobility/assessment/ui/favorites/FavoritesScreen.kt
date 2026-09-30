package ae.qmobility.assessment.ui.favorites

import ae.qmobility.assessment.R
import ae.qmobility.assessment.ui.components.CollectEffects
import ae.qmobility.assessment.ui.components.FullScreenLoading
import ae.qmobility.assessment.ui.components.FullScreenMessage
import ae.qmobility.assessment.ui.components.GradientHeader
import ae.qmobility.assessment.ui.components.ProductCard
import ae.qmobility.assessment.ui.theme.QMobilityTheme
import ae.qmobility.kmp.presentation.favorites.FavoritesEffect
import ae.qmobility.kmp.presentation.favorites.FavoritesIntent
import ae.qmobility.kmp.presentation.favorites.FavoritesState
import ae.qmobility.kmp.presentation.favorites.FavoritesViewModel
import ae.qmobility.kmp.presentation.model.ProductUi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun FavoritesScreen(
    onOpenDetails: (Long) -> Unit,
    viewModel: FavoritesViewModel = koinViewModel(),
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is FavoritesEffect.NavigateToDetails -> onOpenDetails(effect.id)
        }
    }

    FavoritesContent(state = state, onIntent = viewModel::onIntent)
}

@Composable
fun FavoritesContent(state: FavoritesState, onIntent: (FavoritesIntent) -> Unit) {
    Column(Modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        GradientHeader(
            title = stringResource(R.string.tab_favorites),
        )
        when {
            state.isLoading -> FullScreenLoading()
            state.isEmpty -> FullScreenMessage(
                title = stringResource(R.string.favorites_empty_title),
                body = stringResource(R.string.favorites_empty_body),
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                items(state.products, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onProductCardClicked = { onIntent(FavoritesIntent.ProductClicked(product.id)) },
                        modifier = Modifier.animateItem(),
                        removeProductAction = {
                            IconButton(onClick = { onIntent(FavoritesIntent.RemoveClicked(product.id)) }) {
                                Icon(
                                    Icons.Outlined.Delete,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                )
                            }
                        },
                    )
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesPreview() {
    QMobilityTheme {
        FavoritesContent(
            state = FavoritesState(
                isLoading = false,
                products = listOf(ProductUi(1, "Apple AirPods Max", "Apple", "$549.00", "4.7", "-12%", "")),
            ),
            onIntent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FavoritesEmptyPreview() {
    QMobilityTheme { FavoritesContent(state = FavoritesState(isLoading = false), onIntent = {}) }
}
