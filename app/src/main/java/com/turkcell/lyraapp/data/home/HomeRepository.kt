package com.turkcell.lyraapp.data.home

interface HomeRepository {
    suspend fun getHomeFeed(): Result<HomeFeed>
}