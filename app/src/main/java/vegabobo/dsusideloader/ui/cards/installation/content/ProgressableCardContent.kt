package vegabobo.dsusideloader.ui.cards.installation.content

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.clip
import androidx.compose.material3.MaterialTheme
import androidx.compose.ui.Alignment
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import java.text.NumberFormat
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import vegabobo.dsusideloader.ui.components.buttons.PrimaryButton
import vegabobo.dsusideloader.ui.components.buttons.SecondaryButton

@Composable
fun ProgressableCardContent(
    text: String,
    showProgressBar: Boolean = false,
    isIndeterminate: Boolean = false,
    progress: Float = 0F,
    textFirstButton: String = "",
    textSecondButton: String = "",
    onClickFirstButton: (() -> Unit)? = null,
    onClickSecondButton: (() -> Unit)? = null,
) {
    Text(text = text, style = MaterialTheme.typography.bodyLarge)
    AnimatedVisibility(visible = showProgressBar) {
        Column {
            val progressBarModifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 8.dp)
                .height(6.dp)
                .clip(CircleShape)
            if (isIndeterminate) {
                LinearProgressIndicator(modifier = progressBarModifier)
            } else {
                LinearProgressIndicator(
                    modifier = progressBarModifier,
                    progress = progress.coerceIn(0f, 1f),
                )
                Text(
                    text = NumberFormat.getPercentInstance().format(progress.coerceIn(0f, 1f)),
                    modifier = Modifier.align(Alignment.End),
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }
    }
    Spacer(modifier = Modifier.padding(top = 4.dp))
    Row {
        Spacer(modifier = Modifier.weight(1F))
        if (onClickSecondButton != null) {
            SecondaryButton(
                text = textSecondButton,
                onClick = onClickSecondButton,
            )
        }
        if (onClickFirstButton != null && onClickSecondButton != null) {
            Spacer(modifier = Modifier.padding(end = 6.dp))
        }
        if (onClickFirstButton != null) {
            PrimaryButton(
                text = textFirstButton,
                onClick = onClickFirstButton,
            )
        }
    }
}
