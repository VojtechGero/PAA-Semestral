package com.example.semestral.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.semestral.R

@Composable
fun WatchlistApp(onOpenShow: (Int) -> Unit, viewModel: WatchlistViewModel = viewModel()) {
    val navController = rememberNavController()
    val backStack by navController.currentBackStackEntryAsState()
    val currentRoute = backStack?.destination?.route ?: "library"
    val library by viewModel.library.collectAsStateWithLifecycle()
    val search by viewModel.search.collectAsStateWithLifecycle()
    val query by viewModel.query.collectAsStateWithLifecycle()
    val filter by viewModel.filter.collectAsStateWithLifecycle()
    val addingIds by viewModel.addingIds.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    val snackbar = remember { SnackbarHostState() }
    val messageText = message?.let { stringResource(it) }

    LaunchedEffect(messageText) {
        messageText?.let { snackbar.showSnackbar(it); viewModel.dismissMessage() }
    }

    fun navigate(route: String) {
        navController.navigate(route) {
            popUpTo(navController.graph.findStartDestination().id) { saveState = true }
            launchSingleTop = true
            restoreState = true
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbar) },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = currentRoute == "library", onClick = { navigate("library") },
                    icon = { AppIcon(R.drawable.ic_library) }, label = { Text(stringResource(R.string.my_list)) })
                NavigationBarItem(selected = currentRoute == "search", onClick = { navigate("search") },
                    icon = { AppIcon(R.drawable.ic_search) }, label = { Text(stringResource(R.string.search)) })
            }
        }
    ) { padding ->
        NavHost(navController, startDestination = "library", modifier = Modifier.padding(padding)) {
            composable("library") {
                LibraryScreen(library, filter, viewModel::setFilter, onOpenShow,
                    onSearch = { navigate("search") }, onRetry = viewModel::loadLibrary)
            }
            composable("search") {
                SearchScreen(search, query, library.shows.associate { it.id to it.status }, addingIds,
                    viewModel::setQuery, viewModel::searchShows, onOpenShow, viewModel::add)
            }
        }
    }
}
