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
    val roundedSwitches: Boolean,
    val leftAlignedTitle: Boolean,
)

fun DesignStyle.metrics(): StyleMetrics = when (this) {
    DesignStyle.MATERIAL -> StyleMetrics(24, 14, 14, true, false, false)
    DesignStyle.MIUIX -> StyleMetrics(22, 16, 18, false, true, true)
    DesignStyle.CUPERTINO -> StyleMetrics(16, 12, 50, false, true, false)
    DesignStyle.MINIMAL -> StyleMetrics(8, 8, 8, true, false, true)
}

fun DesignStyle.typography(): Typography = when (this) {
    DesignStyle.MATERIAL -> Typography
    DesignStyle.MIUIX -> Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 24.sp, lineHeight = 30.sp, fontWeight = FontWeight.Bold),
        titleMedium = Typography.titleMedium.copy(fontWeight = FontWeight.Medium),
        labelLarge = Typography.labelLarge.copy(fontSize = 15.sp, fontWeight = FontWeight.SemiBold),
    )
    DesignStyle.CUPERTINO -> Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 22.sp, fontWeight = FontWeight.Bold),
        bodyLarge = Typography.bodyLarge.copy(fontSize = 17.sp, lineHeight = 23.sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = 15.sp, lineHeight = 21.sp),
        labelLarge = Typography.labelLarge.copy(fontSize = 15.sp),
    )
    DesignStyle.MINIMAL -> Typography.copy(
        titleLarge = Typography.titleLarge.copy(fontSize = 20.sp, lineHeight = 26.sp),
        titleMedium = Typography.titleMedium.copy(fontSize = 16.sp, lineHeight = 22.sp),
        bodyLarge = Typography.bodyLarge.copy(fontSize = 14.sp, lineHeight = 20.sp),
        bodyMedium = Typography.bodyMedium.copy(fontSize = 13.sp, lineHeight = 19.sp),
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
        DesignStyle.CUPERTINO -> {
            background = Color(if (dark) 0xFF000000 else 0xFFF2F2F7)
            surface = Color(if (dark) 0xFF1C1C1E else 0xFFFFFFFF)
            variant = Color(if (dark) 0xFF2C2C2E else 0xFFE5E5EA)
            foreground = Color(if (dark) 0xFFF2F2F7 else 0xFF1C1C1E)
            muted = Color(if (dark) 0xFFAEAEB2 else 0xFF636366)
            border = Color(if (dark) 0xFF3A3A3C else 0xFFD1D1D6)
        }
        DesignStyle.MINIMAL -> {
            background = Color(if (dark) 0xFF121212 else 0xFFFFFFFF)
            surface = Color(if (dark) 0xFF181818 else 0xFFFFFFFF)
            variant = Color(if (dark) 0xFF242424 else 0xFFF4F4F4)
            foreground = Color(if (dark) 0xFFF5F5F5 else 0xFF1A1A1A)
            muted = Color(if (dark) 0xFFBDBDBD else 0xFF5F5F5F)
            border = Color(if (dark) 0xFF484848 else 0xFFC9C9C9)
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
