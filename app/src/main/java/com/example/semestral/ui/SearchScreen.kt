package com.example.semestral.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.example.semestral.R
import com.example.semestral.data.SavedShow
import com.example.semestral.data.WatchStatus

@Composable
fun SearchScreen(
    state: SearchState,
    query: String,
    savedStatuses: Map<Int, WatchStatus>,
    addingIds: Set<Int>,
    onQueryChange: (String) -> Unit,
    onSearch: () -> Unit,
    onOpenShow: (Int) -> Unit,
    onAdd: (SavedShow) -> Unit
) {
    val keyboard = LocalSoftwareKeyboardController.current
    val submit = { keyboard?.hide(); onSearch() }
    LazyColumn(Modifier.fillMaxSize().imePadding().widthIn(max = 840.dp), contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Text(stringResource(R.string.discover_title), style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold)
            Text(stringResource(R.string.search_subtitle), style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(24.dp))
            OutlinedTextField(value = query, onValueChange = onQueryChange, modifier = Modifier.fillMaxWidth(),
                label = { Text(stringResource(R.string.search_hint)) }, singleLine = true,
                leadingIcon = { AppIcon(R.drawable.ic_search) },
                trailingIcon = {
                    if (query.isNotEmpty()) IconButton(onClick = { onQueryChange("") }) {
                        AppIcon(R.drawable.ic_close, stringResource(R.string.clear_search))
                    }
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = { submit() }))
            Spacer(Modifier.height(10.dp))
            Button(onClick = { submit() }, modifier = Modifier.fillMaxWidth(),
                enabled = query.isNotBlank() && !state.loading) { Text(stringResource(R.string.search)) }
        }
        when {
            state.loading -> item {
                Box(Modifier.fillMaxWidth().padding(48.dp), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
            }
            state.error != null -> item {
                EmptyState(R.drawable.ic_search, stringResource(R.string.load_failed), stringResource(state.error),
                    action = stringResource(R.string.retry), onAction = { submit() })
            }
            !state.searched -> item {
                EmptyState(R.drawable.ic_search, stringResource(R.string.search_start_title), stringResource(R.string.search_start_body))
            }
            state.results.isEmpty() -> item {
                EmptyState(R.drawable.ic_search, stringResource(R.string.no_results_title), stringResource(R.string.no_results_body))
            }
            else -> {
                item {
                    Text(stringResource(R.string.results_count, state.results.size), style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                items(state.results, key = { it.id }) { show ->
                    ShowCard(show, { onOpenShow(show.id) }, savedStatuses[show.id], { onAdd(show) }, show.id in addingIds)
                }
            }
        }
        item { Attribution() }
    }
}
