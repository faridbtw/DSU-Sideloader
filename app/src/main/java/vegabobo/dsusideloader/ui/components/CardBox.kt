package vegabobo.dsusideloader.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import vegabobo.dsusideloader.preferences.DesignStyle
import top.yukonga.miuix.kmp.basic.Card as MiuixCard
import top.yukonga.miuix.kmp.basic.CardDefaults as MiuixCardDefaults
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.theme.LocalDesignStyle
import vegabobo.dsusideloader.ui.theme.metrics

@Composable
fun CardBox(
    modifier: Modifier = Modifier,
    cardTitle: String = "",
    cardIcon: ImageVector? = null,
    addToggle: Boolean = false,
    isToggleChecked: Boolean = false,
    isToggleEnabled: Boolean = true,
    addPadding: Boolean = true,
    cardColor: Color = MaterialTheme.colorScheme.surface,
    onCheckedChange: ((Boolean) -> Unit) = {},
    roundedCornerShape: RoundedCornerShape = RoundedCornerShape(LocalDesignStyle.current.metrics().cardRadius.dp),
    content: @Composable (ColumnScope) -> Unit,
) {
    val body: @Composable () -> Unit = {
        Column(modifier = modifier) {
            if (cardTitle.isNotEmpty()) {
                if (addToggle) {
                    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        CardTitle(Modifier.weight(1F), cardTitle = cardTitle, icon = cardIcon)
                        Spacer(modifier = Modifier.padding(4.dp))
                        StyledSwitch(
                            checked = isToggleChecked,
                            onCheckedChange = onCheckedChange,
                            enabled = isToggleEnabled,
                        )
                    }
                } else {
                    CardTitle(
                        cardTitle = cardTitle, icon = cardIcon,
                        modifier = Modifier.padding(top = 9.5.dp, bottom = 9.5.dp),
                    )
                }
            }
            content(this)
        }
    }
    if (LocalDesignStyle.current == DesignStyle.MIUIX) {
        MiuixCard(
            modifier = Modifier.fillMaxWidth(),
            insideMargin = PaddingValues(if (addPadding) 16.dp else 0.dp),
            colors = MiuixCardDefaults.defaultColors(color = cardColor, contentColor = MaterialTheme.colorScheme.onSurface),
        ) { body() }
    } else {
        Box(
            modifier = Modifier.fillMaxWidth()
                .clip(roundedCornerShape)
                .border(1.dp, if (LocalDesignStyle.current.metrics().borderedCards) MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.65f) else Color.Transparent, roundedCornerShape)
                .background(cardColor)
                .then(if (addPadding) Modifier.padding(20.dp) else Modifier),
        ) { body() }
    }
}
