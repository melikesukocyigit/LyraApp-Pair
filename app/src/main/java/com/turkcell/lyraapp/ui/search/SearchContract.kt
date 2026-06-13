package com.turkcell.lyraapp.ui.search

import com.turkcell.lyraapp.data.search.Genre

data class SearchUiState(
    val isLoading: Boolean = false,
    val query: String = "",
    val selectedFilter: String? = null,
    val allGenres: List<Genre> = emptyList(),
    val filteredGenres: List<Genre> = emptyList(),
)

sealed interface SearchIntent {
    data class QueryChanged(val value: String) : SearchIntent
    data class FilterSelected(val filter: String?) : SearchIntent
}

sealed interface SearchEffect
