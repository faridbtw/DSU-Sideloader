package vegabobo.dsusideloader.ui.theme

import androidx.compose.material3.ColorScheme
import androidx.compose.material3.Typography
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.preferences.DesignStyle

val LocalScrollAnimation = staticCompositionLocalOf { true }

val LocalDesignStyle = staticCompositionLocalOf { DesignStyle.MATERIAL }

data class StyleMetrics(
    val cardRadius: Int,
    val controlRadius: Int,
    val buttonRadius: Int,
    val borderedCards: Boolean,
    val leftAlignedTitle: Boolean,
)

fun DesignStyle.metrics(): StyleMetrics = when (this) {
    DesignStyle.MATERIAL -> StyleMetrics(24, 14, 14, true, false)
    DesignStyle.MIUIX -> StyleMetrics(22, 16, 18, false, true)
}

fun DesignStyle.typography(): Typography = when (this) {
    DesignStyle.MATERIAL -> Typography
    DesignStyle.MIUIX -> Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
        titleMedium = Typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        labelLarge = Typography.labelLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    )

}

fun ColorScheme.withDesignStyle(style: DesignStyle, dark: Boolean): ColorScheme {
    if (style == DesignStyle.MATERIAL) return this
    val background: Color
    val surface: Color
    val variant: Color
    val foreground: Color
    val muted: Color
    val border: Color
    when (style) {
        DesignStyle.MIUIX -> {
            background = Color(if (dark) 0xFF101010 else 0xFFF5F5F5)
            surface = Color(if (dark) 0xFF1D1D1F else 0xFFFFFFFF)
            variant = Color(if (dark) 0xFF2A2A2C else 0xFFF0F0F0)
            foreground = Color(if (dark) 0xFFF2F2F7 else 0xFF1A1A1A)
            muted = Color(if (dark) 0xFFB4B4B9 else 0xFF666666)
            border = Color(if (dark) 0xFF343437 else 0xFFE6E6E6)
        }
        DesignStyle.MATERIAL -> return this
    }
    return copy(
        background = background,
        onBackground = foreground,
        surface = surface,
        onSurface = foreground,
        surfaceVariant = variant,
        onSurfaceVariant = muted,
        inverseOnSurface = surface,
        outlineVariant = border,
    )
}
