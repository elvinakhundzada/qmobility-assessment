package ae.qmobility.assessment.ui.details

import ae.qmobility.assessment.R
import ae.qmobility.assessment.ui.components.BrandTopBar
import ae.qmobility.assessment.ui.components.CollectEffects
import ae.qmobility.assessment.ui.components.DiscountBadge
import ae.qmobility.assessment.ui.components.FullScreenError
import ae.qmobility.assessment.ui.components.FullScreenLoading
import ae.qmobility.assessment.ui.theme.BrandBlue
import ae.qmobility.assessment.ui.theme.BrandGreen
import ae.qmobility.assessment.ui.theme.QMobilityTheme
import ae.qmobility.assessment.ui.theme.RatingStar
import ae.qmobility.kmp.presentation.details.ProductDetailsEffect
import ae.qmobility.kmp.presentation.details.ProductDetailsIntent
import ae.qmobility.kmp.presentation.details.ProductDetailsState
import ae.qmobility.kmp.presentation.details.ProductDetailsViewModel
import ae.qmobility.kmp.presentation.model.ProductDetailsUi
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalResources
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun ProductDetailsScreen(
    productId: Long,
    onBack: () -> Unit,
    viewModel: ProductDetailsViewModel = koinViewModel { parametersOf(productId) },
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val resources = LocalResources.current

    CollectEffects(viewModel.effects) { effect ->
        when (effect) {
            is ProductDetailsEffect.FavoriteToggled -> {
                snackbarHostState.currentSnackbarData?.dismiss()
                snackbarHostState.showSnackbar(
                    resources.getString(
                        if (effect.isFavorite) R.string.details_favorite_added else R.string.details_favorite_removed,
                    ),
                )
            }
        }
    }

    ProductDetailsContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBack = onBack,
        snackbarHostState = snackbarHostState,
    )
}

@Composable
fun ProductDetailsContent(
    state: ProductDetailsState,
    onIntent: (ProductDetailsIntent) -> Unit,
    onBack: () -> Unit,
    snackbarHostState: SnackbarHostState = remember { SnackbarHostState() },
) {
    Scaffold(
        topBar = {
            BrandTopBar(
                title = state.product?.title ?: stringResource(R.string.details_title),
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                },
            )
        },
        floatingActionButton = {
            AnimatedVisibility(
                visible = state.product != null,
                enter = scaleIn() + fadeIn(),
                exit = scaleOut() + fadeOut(),
            ) {
                FavoriteButton(
                    isFavorite = state.isFavorite,
                    onClick = { onIntent(ProductDetailsIntent.ToggleFavorite) },
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
    ) { padding ->
        val modifier = Modifier.padding(padding).fillMaxSize()
        val product = state.product
        val error = state.error
        when {
            product != null -> ProductDetailsBody(product, modifier)
            error != null -> FullScreenError(error, onRetry = { onIntent(ProductDetailsIntent.Retry) }, modifier)
            else -> FullScreenLoading(modifier)
        }
    }
}

@Composable
private fun FavoriteButton(isFavorite: Boolean, onClick: () -> Unit) {
    val containerColor by animateColorAsState(
        targetValue = if (isFavorite) BrandGreen else BrandBlue,
        label = "favoriteContainer",
    )
    FloatingActionButton(
        onClick = onClick,
        containerColor = containerColor,
        contentColor = Color.White,
        shape = CircleShape,
    ) {
        AnimatedContent(
            targetState = isFavorite,
            transitionSpec = { scaleIn() togetherWith scaleOut() },
            label = "favoriteIcon",
        ) { favorite ->
            Icon(
                imageVector = if (favorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                contentDescription = null,
            )
        }
    }
}

@Composable
private fun ProductDetailsBody(product: ProductDetailsUi, modifier: Modifier = Modifier) {
    Column(modifier.verticalScroll(rememberScrollState())) {
        ImageGallery(product.images)
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            TitleSection(product)
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                StatTile(
                    icon = Icons.Filled.Star,
                    iconTint = RatingStar,
                    value = product.rating,
                    label = stringResource(R.string.details_rating),
                    modifier = Modifier.weight(1f),
                )
                StatTile(
                    icon = Icons.Filled.ShoppingCart,
                    value = product.stock.toString(),
                    label = stringResource(R.string.details_stock),
                    modifier = Modifier.weight(1f),
                )
                product.availability?.let {
                    StatTile(
                        icon = Icons.Filled.CheckCircle,
                        value = it,
                        label = stringResource(R.string.details_availability),
                        modifier = Modifier.weight(1f),
                    )
                }
            }
            SectionCard(title = stringResource(R.string.details_description)) {
                Text(
                    product.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Spacer(Modifier.height(72.dp))
        }
    }
}

@Composable
private fun TitleSection(product: ProductDetailsUi) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        Text(
            listOfNotNull(product.brand, product.category).joinToString(" · "),
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.secondary,
        )
        Text(product.title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(
                product.price,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            product.originalPrice?.let {
                Text(
                    it,
                    style = MaterialTheme.typography.bodyLarge,
                    textDecoration = TextDecoration.LineThrough,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            product.discount?.let { DiscountBadge(it) }
        }
    }
}

@Composable
private fun ImageGallery(images: List<String>) {
    if (images.isEmpty()) return
    val pagerState = rememberPagerState { images.size }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(bottomStart = 28.dp, bottomEnd = 28.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth().aspectRatio(1f),
        ) { page ->
            AsyncImage(
                model = images[page],
                contentDescription = null,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize().padding(24.dp),
            )
        }
        if (images.size > 1) {
            PageIndicator(
                count = images.size,
                current = pagerState.currentPage,
                modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 16.dp),
            )
        }
    }
}

@Composable
private fun PageIndicator(count: Int, current: Int, modifier: Modifier = Modifier) {
    Row(modifier, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(count) { index ->
            val selected = index == current
            val width by animateDpAsState(if (selected) 20.dp else 8.dp, label = "indicatorWidth")
            Box(
                Modifier
                    .size(width = width, height = 8.dp)
                    .clip(CircleShape)
                    .background(if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
            )
        }
    }
}

@Composable
private fun StatTile(
    icon: ImageVector,
    value: String,
    label: String,
    modifier: Modifier = Modifier,
    iconTint: Color = MaterialTheme.colorScheme.secondary,
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(vertical = 14.dp, horizontal = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Icon(icon, contentDescription = null, tint = iconTint, modifier = Modifier.size(22.dp))
            Text(
                value,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun SectionCard(title: String, content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
    ) {
        Column(Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
            content()
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun ProductDetailsPreview() {
    QMobilityTheme {
        ProductDetailsContent(
            state = ProductDetailsState(
                isLoading = false,
                isFavorite = true,
                product = ProductDetailsUi(
                    id = 1,
                    title = "Apple AirPods Max",
                    description = "Over-ear headphones with active noise cancellation.",
                    brand = "Apple",
                    category = "Mobile accessories",
                    price = "$479.00",
                    originalPrice = "$549.00",
                    discount = "-13%",
                    rating = "4.7",
                    stock = 12,
                    availability = "In Stock",
                    images = emptyList(),
                ),
            ),
            onIntent = {},
            onBack = {},
        )
    }
}
