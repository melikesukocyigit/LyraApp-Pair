package com.turkcell.lyraapp.data.search

interface SearchRepository {
    suspend fun getSearchFeed(): Result<SearchFeed>
}
