package com.turkcell.lyraapp.data.search

data class SearchFeed(
    val genres: List<Genre>,
)

data class Genre(
    val id: String,
    val name: String,
    val startColor: Long,
    val endColor: Long,
)
