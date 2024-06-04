package ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.Colors
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

@Composable
fun colorScheme(): Colors {
   return if (isSystemInDarkTheme()) DarkColorPalette else LightColorPalette
}

val LightColorPalette = lightColors(
    primary = Color(0xFFECEFF1),
    primaryVariant = Color(0xFFCFD8DC),
    secondary = Color(0xFFB0BEC5),
    background = Color(0xFFFFFFFF),
    surface = Color(0xFFFAFAFA),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFF2C2C2C),
    onSecondary = Color(0xFF2C2C2C),
    onBackground = Color(0xFF2C2C2C),
    onSurface = Color(0xFF2C2C2C),
    onError = Color(0xFFE0E0E0),
)

val DarkColorPalette = darkColors(
    primary = Color(0xFF263238),
    primaryVariant = Color(0xFF37474F),
    secondary = Color(0xFF455A64),
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFCF6679),
    onPrimary = Color(0xFFD3D3D3),
    onSecondary = Color(0xFFD3D3D3),
    onBackground = Color(0xFFD3D3D3),
    onSurface = Color(0xFFD3D3D3),
    onError = Color(0xFF2C2C2C),
)