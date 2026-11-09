package com.filex.studio.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.filex.studio.theme.FxPalette
import com.filex.studio.theme.LocalFxSpacing

/** Scrollable page with a title. The surrounding Scaffold already applies system-bar insets. */
@Composable
fun FxScreen(title: String, content: @Composable ColumnScope.() -> Unit) {
    val sp = LocalFxSpacing.current
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = sp.md),
        verticalArrangement = Arrangement.spacedBy(sp.md),
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(top = sp.md),
        )
        content()
        Spacer(Modifier.height(sp.sm))
    }
}

@Composable
fun FxCard(modifier: Modifier = Modifier, content: @Composable ColumnScope.() -> Unit) {
    val sp = LocalFxSpacing.current
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
    ) {
        Column(
            modifier = Modifier.padding(sp.md),
            verticalArrangement = Arrangement.spacedBy(sp.sm),
            content = content,
        )
    }
}

@Composable
fun FxSectionTitle(text: String) {
    Text(text = text, style = MaterialTheme.typography.titleMedium)
}

@Composable
fun FxMutedText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
    )
}

/** Label on the left, value on the right. [ok] colors the value green/red; null leaves it neutral. */
@Composable
fun FxStatusRow(label: String, value: String, ok: Boolean? = null) {
    val valueColor = when (ok) {
        true -> Color(0xFF4ADE80)
        false -> FxPalette.error
        null -> MaterialTheme.colorScheme.onSurface
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1f),
        )
    }
}
