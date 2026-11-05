package vegabobo.dsusideloader.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.graphics.vector.ImageVector

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    scrollBehavior: TopAppBarScrollBehavior? = null,
    barTitle: String,
    icon: ImageVector? = null,
    iconContentDescription: String? = null,
    onClickIcon: () -> Unit = {},
    onClickBackButton: (() -> Unit)? = null,
) {
    CenterAlignedTopAppBar(
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.background,
            scrolledContainerColor = MaterialTheme.colorScheme.background,
        ),
        title = { Text(text = barTitle, style = MaterialTheme.typography.titleMedium) },
        navigationIcon = {
            if (onClickBackButton != null) {
                IconButton(onClickBackButton) {
                    Icon(
                        imageVector = Icons.Outlined.ArrowBack,
                        contentDescription = iconContentDescription ?: stringResource(vegabobo.dsusideloader.R.string.navigate_back),
                    )
                }
            }
        },
        actions = {
            if (icon != null) {
                IconButton(onClickIcon) {
                    Icon(
                        imageVector = icon,
                        contentDescription = iconContentDescription,
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
    )
}
