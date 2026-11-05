package vegabobo.dsusideloader.ui.theme

import androidx.compose.ui.graphics.Color
import vegabobo.dsusideloader.preferences.AccentColor

// Paired foregrounds and containers keep accents readable in both modes.
data class AccentPalette(
    val lightPrimary: Color,
    val darkPrimary: Color,
    val darkOnPrimary: Color,
    val lightContainer: Color,
    val darkContainer: Color,
    val lightOnContainer: Color,
    val darkOnContainer: Color,
)

fun AccentColor.palette(): AccentPalette = when (this) {
    AccentColor.INDIGO -> AccentPalette(Indigo, IndigoLight, Color(0xFF282375), Color(0xFFEBE9FF), Color(0xFF302D66), Color(0xFF302B78), Color(0xFFE5E2FF))
    AccentColor.BLUE -> AccentPalette(Color(0xFF245DB5), Color(0xFFADC7FF), Color(0xFF002F65), Color(0xFFD8E5FF), Color(0xFF17427F), Color(0xFF143A74), Color(0xFFDCE7FF))
    AccentColor.TEAL -> AccentPalette(Color(0xFF006B60), Color(0xFF7DD9CA), Color(0xFF00382F), Color(0xFFBCF2E7), Color(0xFF005047), Color(0xFF004D43), Color(0xFFA2F2E3))
    AccentColor.GREEN -> AccentPalette(Color(0xFF376B30), Color(0xFFA2D592), Color(0xFF10390D), Color(0xFFD5EFCA), Color(0xFF235020), Color(0xFF224D1C), Color(0xFFBDEFAE))
    AccentColor.ROSE -> AccentPalette(Color(0xFFA23662), Color(0xFFFFB0CC), Color(0xFF620F34), Color(0xFFFFD9E5), Color(0xFF7B2249), Color(0xFF721F43), Color(0xFFFFD9E5))
    AccentColor.AMBER -> AccentPalette(Color(0xFF825500), Color(0xFFF4BE62), Color(0xFF442C00), Color(0xFFFFDEA8), Color(0xFF604000), Color(0xFF603E00), Color(0xFFFFDEA8))
}
