package com.example.semestral.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.text.HtmlCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.semestral.R
import com.example.semestral.data.WatchStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DetailScreen(onBack: () -> Unit, viewModel: DetailViewModel = viewModel()) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var confirmRemove by rememberSaveable { mutableStateOf(false) }
    val snackbar = remember { SnackbarHostState() }
    val messageText = state.message?.let { stringResource(it) }
    val uriHandler = LocalUriHandler.current
    LaunchedEffect(state.removed) { if (state.removed) onBack() }
    LaunchedEffect(messageText) {
        messageText?.let { snackbar.showSnackbar(it); viewModel.dismissMessage() }
    }

    Scaffold(topBar = {
        TopAppBar(title = { Text(stringResource(R.string.show_detail)) }, navigationIcon = {
            IconButton(onClick = onBack) { AppIcon(R.drawable.ic_back, stringResource(R.string.back)) }
        })
    }, snackbarHost = { SnackbarHost(snackbar) }) { padding ->
        when {
            state.loading -> Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
            state.error != null -> Column(Modifier.padding(padding).verticalScroll(rememberScrollState())) {
                EmptyState(R.drawable.ic_tv, stringResource(R.string.load_failed), stringResource(state.error!!),
                    action = stringResource(R.string.retry), onAction = viewModel::load)
            }
            else -> state.show?.let { show ->
                val summary = remember(show.summary) {
                    HtmlCompat.fromHtml(show.summary, HtmlCompat.FROM_HTML_MODE_COMPACT).toString().trim()
                }
                Column(Modifier.padding(padding).fillMaxSize().verticalScroll(rememberScrollState())) {
                    Column(Modifier.widthIn(max = 840.dp).align(Alignment.CenterHorizontally).padding(20.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Poster(show, Modifier.width(160.dp).height(225.dp).align(Alignment.CenterHorizontally))
                        Text(show.name, style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        val metadata = listOfNotNull(show.premiered?.take(4), show.genres.takeIf { it.isNotBlank() })
                        if (metadata.isNotEmpty()) Text(metadata.joinToString(" · "),
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        show.rating?.let { Text(stringResource(R.string.rating_value, it),
                            color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.titleSmall) }

                        if (state.isSaved) {
                            HorizontalDivider()
                            Text(stringResource(R.string.your_status), style = MaterialTheme.typography.titleMedium)
                            Text(stringResource(R.string.status_help), style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Column(Modifier.fillMaxWidth().selectableGroup()) {
                                WatchStatus.entries.forEach { status ->
                                    Row(Modifier.fillMaxWidth().selectable(
                                        selected = show.status == status, enabled = !state.busy,
                                        role = Role.RadioButton, onClick = { viewModel.changeStatus(status) }
                                    ).padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(selected = show.status == status, onClick = null, enabled = !state.busy)
                                        Spacer(Modifier.width(12.dp))
                                        Text(stringResource(status.label()))
                                    }
                                }
                            }
                        } else {
                            Button(onClick = viewModel::add, enabled = !state.busy, modifier = Modifier.fillMaxWidth()) {
                                AppIcon(R.drawable.ic_add)
                                Spacer(Modifier.width(8.dp))
                                Text(stringResource(R.string.add_show))
                            }
                        }
                        HorizontalDivider()
                        Text(stringResource(R.string.about_show), style = MaterialTheme.typography.titleLarge)
                        Text(summary.ifBlank { stringResource(R.string.no_summary) }, style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(R.string.api_language_note), style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant)
                        OutlinedButton(onClick = { uriHandler.openUri(show.tvmazeUrl) }, modifier = Modifier.fillMaxWidth()) {
                            Text(stringResource(R.string.open_tvmaze))
                        }
                        if (state.isSaved) {
                            TextButton(onClick = { confirmRemove = true }, enabled = !state.busy,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                                Text(stringResource(R.string.remove_show))
                            }
                        }
                        Attribution()
                    }
                }
                if (confirmRemove) AlertDialog(
                    onDismissRequest = { confirmRemove = false },
                    title = { Text(stringResource(R.string.remove_title)) },
                    text = { Text(stringResource(R.string.remove_body, show.name)) },
                    confirmButton = {
                        TextButton(onClick = { confirmRemove = false; viewModel.remove() }) {
                            Text(stringResource(R.string.remove_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { confirmRemove = false }) { Text(stringResource(R.string.cancel)) }
                    }
                )
            }
        }
    }
}

@Composable
fun Attribution() {
    val uriHandler = LocalUriHandler.current
    TextButton(onClick = { uriHandler.openUri("https://www.tvmaze.com/api#licensing") }, modifier = Modifier.fillMaxWidth()) {
        Text(stringResource(R.string.attribution), style = MaterialTheme.typography.labelSmall)
    }
}
