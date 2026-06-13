package com.turkcell.lyraapp.ui.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.turkcell.lyraapp.data.search.Genre
import com.turkcell.lyraapp.data.search.SearchRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchRepository: SearchRepository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SearchUiState())
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()

    private val _effect = Channel<SearchEffect>(Channel.BUFFERED)
    val effect: Flow<SearchEffect> = _effect.receiveAsFlow()

    init {
        loadFeed()
    }

    fun onIntent(intent: SearchIntent) {
        when (intent) {
            is SearchIntent.QueryChanged -> _uiState.update { it.copy(query = intent.value) }
            is SearchIntent.FilterSelected -> applyFilter(intent.filter)
        }
    }

    private fun loadFeed() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            searchRepository.getSearchFeed()
                .onSuccess { feed ->
                    _uiState.update { state ->
                        state.copy(
                            isLoading = false,
                            allGenres = feed.genres,
                            filteredGenres = feed.genres,
                        )
                    }
                }
                .onFailure {
                    _uiState.update { it.copy(isLoading = false) }
                }
        }
    }

    private fun applyFilter(filter: String?) {
        _uiState.update { state ->
            state.copy(
                selectedFilter = filter,
                filteredGenres = deriveFilteredGenres(state.allGenres, filter),
            )
        }
    }

    private fun deriveFilteredGenres(all: List<Genre>, filter: String?): List<Genre> =
        if (filter == null) all else all.filter { it.name == filter }
}
