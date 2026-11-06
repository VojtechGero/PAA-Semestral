package com.example.semestral.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.semestral.R
import com.example.semestral.WatchlistApplication
import com.example.semestral.data.SavedShow
import com.example.semestral.data.WatchStatus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

data class LibraryState(
    val shows: List<SavedShow> = emptyList(),
    val loading: Boolean = true,
    val failed: Boolean = false
)

data class SearchState(
    val results: List<SavedShow> = emptyList(),
    val loading: Boolean = false,
    val searched: Boolean = false,
    val error: Int? = null
)

class WatchlistViewModel(application: Application, private val savedState: SavedStateHandle) :
    AndroidViewModel(application) {
    private val app = application as WatchlistApplication
    private val repository = app.repository
    private val _library = MutableStateFlow(LibraryState())
    val library = _library.asStateFlow()
    private val _search = MutableStateFlow(SearchState())
    val search = _search.asStateFlow()
    val query = savedState.getStateFlow("query", "")
    val filter = savedState.getStateFlow("filter", app.preferences.getString("filter", "ALL") ?: "ALL")
    private val _addingIds = MutableStateFlow(emptySet<Int>())
    val addingIds = _addingIds.asStateFlow()
    private val _message = MutableStateFlow<Int?>(null)
    val message = _message.asStateFlow()
    private var searchJob: Job? = null

    init {
        loadLibrary()
        if (query.value.isNotBlank()) searchShows()
    }

    fun loadLibrary() {
        _library.value = _library.value.copy(loading = true, failed = false)
        viewModelScope.launch {
            try {
                repository.shows.collect { _library.value = LibraryState(shows = it, loading = false) }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _library.value = LibraryState(loading = false, failed = true)
            }
        }
    }

    fun setFilter(status: WatchStatus?) {
        val value = status?.name ?: "ALL"
        savedState["filter"] = value
        app.preferences.edit().putString("filter", value).apply()
    }

    fun setQuery(value: String) {
        searchJob?.cancel()
        savedState["query"] = value.take(100)
        _search.value = SearchState()
    }

    fun searchShows() {
        val text = query.value.trim()
        if (text.isEmpty()) return
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            _search.value = SearchState(loading = true)
            try {
                _search.value = SearchState(results = repository.search(text), searched = true)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _search.value = SearchState(searched = true, error = networkError(e))
            }
        }
    }

    fun add(show: SavedShow) {
        if (show.id in _addingIds.value) return
        _addingIds.value += show.id
        viewModelScope.launch {
            try {
                repository.add(show)
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _message.value = R.string.storage_error
            } finally {
                _addingIds.value -= show.id
            }
        }
    }

    fun dismissMessage() { _message.value = null }
}

internal fun networkError(error: Exception): Int = when {
    error is HttpException && error.code() == 429 -> R.string.rate_limit_error
    error is HttpException && error.code() == 404 -> R.string.not_found_error
    else -> R.string.network_error
}
