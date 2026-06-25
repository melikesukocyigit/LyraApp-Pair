package com.turkcell.lyraapp.di

import com.turkcell.lyraapp.data.library.InMemoryPlaylistFavoritesRepository
import com.turkcell.lyraapp.data.library.PlaylistFavoritesRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class PlaylistFavoritesModule {

    @Binds
    @Singleton
    abstract fun bindPlaylistFavoritesRepository(
        impl: InMemoryPlaylistFavoritesRepository
    ): PlaylistFavoritesRepository
}
