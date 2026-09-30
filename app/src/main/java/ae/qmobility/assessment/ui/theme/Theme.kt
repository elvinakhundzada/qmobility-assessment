package ae.qmobility.assessment.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BrandBlue,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD5E3F0),
    onPrimaryContainer = Color(0xFF0F2A40),
    secondary = BrandGreen,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFD2F1E8),
    onSecondaryContainer = Color(0xFF0B3D30),
    tertiary = BrandGreen,
    background = Color(0xFFF2F5F8),
    onBackground = Color(0xFF16202A),
    surface = Color.White,
    onSurface = Color(0xFF16202A),
    surfaceVariant = Color(0xFFE6ECF1),
    onSurfaceVariant = Color(0xFF5B6876),
    surfaceContainerHigh = Color(0xFFEDF1F5),
    outlineVariant = Color(0xFFD5DDE4),
)

private val DarkColors = darkColorScheme(
    primary = BrandBlueLight,
    onPrimary = Color(0xFF0B2437),
    primaryContainer = Color(0xFF244560),
    onPrimaryContainer = Color(0xFFD5E3F0),
    secondary = BrandGreenLight,
    onSecondary = Color(0xFF003828),
    secondaryContainer = Color(0xFF1F5446),
    onSecondaryContainer = Color(0xFFD2F1E8),
    tertiary = BrandGreenLight,
    background = Color(0xFF0E151B),
    onBackground = Color(0xFFE3E9EE),
    surface = Color(0xFF172029),
    onSurface = Color(0xFFE3E9EE),
    surfaceVariant = Color(0xFF26323D),
    onSurfaceVariant = Color(0xFFA9B6C2),
    surfaceContainerHigh = Color(0xFF202B35),
    outlineVariant = Color(0xFF34424E),
)

@Composable
fun QMobilityTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = Typography(),
        content = content,
    )
}
