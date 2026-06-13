package com.turkcell.lyraapp.data.search

import kotlinx.coroutines.delay
import javax.inject.Inject

class MockSearchRepository @Inject constructor() : SearchRepository {

    override suspend fun getSearchFeed(): Result<SearchFeed> {
        delay(NETWORK_DELAY_MS)
        return Result.success(SearchFeed(genres = GENRES))
    }

    private companion object {
        const val NETWORK_DELAY_MS = 600L

        val GENRES = listOf(
            Genre("g-1", "Pop",        0xFF4DC4B8, 0xFF2E9B8F),
            Genre("g-2", "Elektronik", 0xFF9B8FD9, 0xFF5B52A0),
            Genre("g-3", "Akustik",    0xFF9B5CC4, 0xFF6B3494),
            Genre("g-4", "Lo-fi",      0xFF2E7D72, 0xFF1A4E49),
            Genre("g-5", "Indie",      0xFF4A4E8C, 0xFF2D3070),
            Genre("g-6", "Jazz",       0xFF5BA85A, 0xFF2E6B2D),
            Genre("g-7", "Klasik",     0xFFBE8AAE, 0xFF8B5578),
            Genre("g-8", "Yolculuk",   0xFFE8956D, 0xFFC46A3E),
        )
    }
}
