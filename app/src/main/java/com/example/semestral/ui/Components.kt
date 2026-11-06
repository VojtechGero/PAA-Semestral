package com.example.semestral.ui

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.semestral.R
import com.example.semestral.data.SavedShow
import com.example.semestral.data.WatchStatus

@StringRes
fun WatchStatus.label(): Int = when (this) {
    WatchStatus.PLAN_TO_WATCH -> R.string.status_planned
    WatchStatus.WATCHING -> R.string.status_watching
    WatchStatus.FINISHED -> R.string.status_finished
}

@Composable
fun AppIcon(@DrawableRes drawable: Int, description: String? = null, modifier: Modifier = Modifier) {
    Icon(painterResource(drawable), contentDescription = description, modifier = modifier)
}

@Composable
fun Poster(show: SavedShow, modifier: Modifier = Modifier) {
    Box(modifier.clip(RoundedCornerShape(12.dp)).background(MaterialTheme.colorScheme.surfaceVariant),
        contentAlignment = Alignment.Center) {
        AppIcon(R.drawable.ic_tv, modifier = Modifier.size(28.dp))
        AsyncImage(
            model = show.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
fun ShowCard(
    show: SavedShow,
    onClick: () -> Unit,
    savedStatus: WatchStatus? = null,
    onAdd: (() -> Unit)? = null,
    adding: Boolean = false
) {
    Card(onClick = onClick, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)) {
        Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)) {
            Poster(show, Modifier.width(76.dp).height(108.dp))
            Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(show.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                Text(listOfNotNull(show.premiered?.take(4), show.genres.takeIf { it.isNotBlank() }).joinToString(" · ")
                    .ifBlank { stringResource(R.string.metadata_unknown) },
                    style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2, overflow = TextOverflow.Ellipsis)
                if (savedStatus != null) {
                    Surface(color = MaterialTheme.colorScheme.secondaryContainer, shape = RoundedCornerShape(6.dp)) {
                        Text(stringResource(savedStatus.label()), Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSecondaryContainer)
                    }
                } else if (show.rating != null) {
                    Text(stringResource(R.string.rating_value, show.rating),
                        style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
            if (onAdd != null) {
                IconButton(onClick = onAdd, enabled = !adding && savedStatus == null) {
                    if (adding) CircularProgressIndicator(Modifier.size(22.dp), strokeWidth = 2.dp)
                    else AppIcon(if (savedStatus == null) R.drawable.ic_add else R.drawable.ic_check,
                        stringResource(if (savedStatus == null) R.string.add_show else R.string.already_saved))
                }
            } else {
                AppIcon(R.drawable.ic_chevron, modifier = Modifier.size(20.dp))
            }
        }
    }
}

@Composable
fun EmptyState(
    @DrawableRes icon: Int,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    action: String? = null,
    onAction: () -> Unit = {}
) {
    Column(modifier.fillMaxWidth().padding(horizontal = 24.dp, vertical = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.secondaryContainer) {
            AppIcon(icon, modifier = Modifier.padding(22.dp).size(36.dp))
        }
        Text(title, style = MaterialTheme.typography.titleLarge,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        Text(description, style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = androidx.compose.ui.text.style.TextAlign.Center)
        if (action != null) Button(onClick = onAction) { Text(action) }
    }
}
