package com.example.semestral.ui

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.semestral.R
import com.example.semestral.data.WatchStatus

@Composable
fun LibraryScreen(
    state: LibraryState,
    filter: String,
    onFilter: (WatchStatus?) -> Unit,
    onOpenShow: (Int) -> Unit,
    onSearch: () -> Unit,
    onRetry: () -> Unit
) {
    val visible = state.shows.filter { filter == "ALL" || it.status.name == filter }
    LazyColumn(Modifier.fillMaxSize().widthIn(max = 840.dp),
        contentPadding = PaddingValues(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween) {
                Text(stringResource(R.string.app_name), style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold)
                FilledTonalIconButton(onClick = onSearch) {
                    AppIcon(R.drawable.ic_add, stringResource(R.string.find_shows))
                }
            }
            Text(stringResource(R.string.library_subtitle), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            Surface(color = MaterialTheme.colorScheme.primaryContainer, shape = RoundedCornerShape(24.dp)) {
                Column(Modifier.fillMaxWidth().padding(22.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(R.string.your_collection), style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(stringResource(R.string.collection_count, state.shows.size),
                        style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                    Text(stringResource(R.string.collection_stats,
                        state.shows.count { it.status == WatchStatus.WATCHING },
                        state.shows.count { it.status == WatchStatus.FINISHED }),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer)
                }
            }
        }
        item {
            Row(Modifier.horizontalScroll(rememberScrollState()), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                FilterChip(selected = filter == "ALL", onClick = { onFilter(null) },
                    label = { Text(stringResource(R.string.filter_all)) })
                WatchStatus.entries.forEach { status ->
                    FilterChip(selected = filter == status.name, onClick = { onFilter(status) },
                        label = { Text(stringResource(status.label())) })
                }
            }
        }
        when {
            state.loading -> item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            state.failed -> item {
                EmptyState(R.drawable.ic_tv, stringResource(R.string.load_failed), stringResource(R.string.storage_error),
                    action = stringResource(R.string.retry), onAction = onRetry)
            }
            state.shows.isEmpty() -> item {
                EmptyState(R.drawable.ic_library, stringResource(R.string.empty_library_title),
                    stringResource(R.string.empty_library_body), action = stringResource(R.string.find_shows), onAction = onSearch)
            }
            visible.isEmpty() -> item {
                EmptyState(R.drawable.ic_tv, stringResource(R.string.empty_filter_title), stringResource(R.string.empty_filter_body))
            }
            else -> items(visible, key = { it.id }) { show ->
                ShowCard(show, onClick = { onOpenShow(show.id) }, savedStatus = show.status)
            }
        }
        item { Attribution() }
    }
}
