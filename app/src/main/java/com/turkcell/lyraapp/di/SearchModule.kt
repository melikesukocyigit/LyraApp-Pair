package com.turkcell.lyraapp.di

import com.turkcell.lyraapp.data.search.MockSearchRepository
import com.turkcell.lyraapp.data.search.SearchRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class SearchModule {

    @Binds
    @Singleton
    abstract fun bindSearchRepository(impl: MockSearchRepository): SearchRepository
}
