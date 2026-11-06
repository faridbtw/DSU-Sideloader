package vegabobo.dsusideloader.ui.screen.settings

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.Cottage
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import vegabobo.dsusideloader.R
import vegabobo.dsusideloader.preferences.AppearanceSettings
import vegabobo.dsusideloader.ui.theme.appearanceColorScheme

/** A small live representation of DSU's workspace, using the active framework's colors. */
@Composable
internal fun ThemePhonePreview() {
    val colors = MaterialTheme.colorScheme
    val description = stringResource(R.string.theme_preview)
    BoxWithConstraints(
        modifier = Modifier.fillMaxWidth().padding(top = 16.dp, bottom = 24.dp)
            .clearAndSetSemantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        Surface(
            modifier = Modifier.width((maxWidth * 0.42f).coerceAtMost(176.dp)).aspectRatio(0.5f),
            shape = RoundedCornerShape(22.dp), color = colors.background,
            border = BorderStroke(1.dp, colors.outlineVariant),
        ) {
            Column(modifier = Modifier.fillMaxSize()) {
                Box(modifier = Modifier.fillMaxWidth().height(20.dp), contentAlignment = Alignment.Center) {
                    Box(Modifier.size(26.dp, 3.dp).background(colors.onBackground.copy(alpha = 0.25f), CircleShape))
                }
                Text(
                    stringResource(R.string.app_name), color = colors.onBackground,
                    fontSize = 10.sp, lineHeight = 12.sp, maxLines = 1,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                )
                Column(
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    PreviewPanel(color = colors.primaryContainer, height = 37) {
                        PreviewLine(colors.onPrimaryContainer, 0.45f)
                        PreviewLine(colors.onPrimaryContainer.copy(alpha = 0.4f), 0.7f)
                    }
                    Surface(shape = RoundedCornerShape(8.dp), color = colors.surface, modifier = Modifier.weight(1f)) {
                        Column(modifier = Modifier.padding(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            PreviewLine(colors.onSurface, 0.6f)
                            Box(Modifier.fillMaxWidth().height(18.dp).background(colors.surfaceVariant, RoundedCornerShape(4.dp)))
                            Spacer(Modifier.weight(1f))
                            Box(Modifier.fillMaxWidth().height(17.dp).background(colors.primary, RoundedCornerShape(5.dp)))
                        }
                    }
                    PreviewPanel(color = colors.surfaceVariant, height = 24) {
                        PreviewLine(colors.onSurfaceVariant, 0.65f)
                    }
                    Spacer(Modifier.height(2.dp))
                }
                Row(
                    modifier = Modifier.fillMaxWidth().background(colors.surfaceContainer).padding(vertical = 10.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    Icon(Icons.Rounded.Cottage, null, tint = colors.primary, modifier = Modifier.size(15.dp))
                    Icon(Icons.Rounded.Settings, null, tint = colors.onSurfaceVariant, modifier = Modifier.size(15.dp))
                }
            }
        }
    }
}

@Composable
private fun PreviewPanel(color: Color, height: Int, content: @Composable () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().height(height.dp).background(color, RoundedCornerShape(7.dp)).padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(5.dp, Alignment.CenterVertically),
    ) { content() }
}

@Composable
private fun PreviewLine(color: Color, fraction: Float) {
    Box(Modifier.fillMaxWidth(fraction).height(3.dp).background(color, CircleShape))
}

@Composable
internal fun ThemeColorStrip(settings: AppearanceSettings, colors: List<Int>, labels: List<String>, onSelect: (Int) -> Unit) {
    val systemDark = isSystemInDarkTheme()
    val wallpaperSeed = MaterialTheme.colorScheme.primary
    LazyRow(
        modifier = Modifier.fillMaxWidth().selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        itemsIndexed(colors, key = { _, color -> color }) { index, seed ->
            val palette = remember(settings, systemDark, seed, wallpaperSeed) {
                appearanceColorScheme(settings.copy(keyColor = seed), systemDark, if (seed == 0) wallpaperSeed else Color(seed))
            }
            val selected = seed == settings.keyColor
            Box(
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(18.dp))
                    .background(palette.surfaceContainer)
                    .selectable(selected = selected, role = Role.RadioButton, onClick = { onSelect(seed) })
                    .semantics { contentDescription = labels[index] },
                contentAlignment = Alignment.Center,
            ) {
                Canvas(Modifier.size(44.dp)) {
                    drawArc(palette.primaryContainer, startAngle = 180f, sweepAngle = 180f, useCenter = true)
                    drawArc(palette.tertiaryContainer, startAngle = 0f, sweepAngle = 180f, useCenter = true)
                }
                if (selected) {
                    Box(
                        Modifier.size(52.dp).border(2.dp, palette.primary, CircleShape),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Rounded.Check, null, tint = palette.onPrimary,
                            modifier = Modifier.size(24.dp).background(palette.primary, CircleShape).padding(4.dp),
                        )
                    }
                } else {
                    Box(Modifier.size(18.dp).background(palette.primary, CircleShape))
                }
            }
        }
    }
}
