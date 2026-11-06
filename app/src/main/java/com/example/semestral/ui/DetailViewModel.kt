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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DetailState(
    val show: SavedShow? = null,
    val isSaved: Boolean = false,
    val loading: Boolean = true,
    val busy: Boolean = false,
    val error: Int? = null,
    val message: Int? = null,
    val removed: Boolean = false
)

class DetailViewModel(application: Application, savedState: SavedStateHandle) : AndroidViewModel(application) {
    private val repository = (application as WatchlistApplication).repository
    private val showId = savedState.get<Int>(SHOW_ID) ?: -1
    private val _state = MutableStateFlow(DetailState())
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            try {
                repository.observeShow(showId).collect { saved ->
                    _state.value = if (saved == null) _state.value.copy(isSaved = false)
                    else _state.value.copy(show = saved, isSaved = true, loading = false, error = null)
                }
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.value = _state.value.copy(message = R.string.storage_error)
            }
        }
        load()
    }

    fun load() {
        _state.value = _state.value.copy(loading = true, error = null)
        viewModelScope.launch {
            try {
                // Saved details open entirely from Room, even in airplane mode.
                val saved = repository.findSaved(showId)
                val show = saved ?: repository.fetchShow(showId)
                _state.value = _state.value.copy(show = show, isSaved = saved != null, loading = false)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _state.value = _state.value.copy(loading = false, error = networkError(e))
            }
        }
    }

    fun add() = write {
        repository.add(requireNotNull(_state.value.show))
        _state.value = _state.value.copy(isSaved = true)
    }

    fun changeStatus(status: WatchStatus) = write {
        repository.changeStatus(showId, status)
    }

    fun remove() = write {
        repository.remove(showId)
        _state.value = _state.value.copy(removed = true)
    }

    private fun write(action: suspend () -> Unit) {
        if (_state.value.busy) return
        _state.value = _state.value.copy(busy = true)
        viewModelScope.launch {
            try {
                action()
            } catch (e: CancellationException) {
                throw e
            } catch (_: Exception) {
                _state.value = _state.value.copy(message = R.string.storage_error)
            } finally {
                _state.value = _state.value.copy(busy = false)
            }
        }
    }

    fun dismissMessage() { _state.value = _state.value.copy(message = null) }

    companion object {
        const val SHOW_ID = "show_id"
    }
}
