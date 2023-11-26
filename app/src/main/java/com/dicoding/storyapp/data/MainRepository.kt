package com.dicoding.storyapp.data

import androidx.lifecycle.LiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.storyapp.remote.response.ListStoryItem
import com.dicoding.storyapp.remote.retrofit.ApiService
import com.dicoding.storyapp.utility.StoriesPagingSource

open class MainRepository(private val apiService: ApiService) {
    fun getStoriesPagingData(): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 20,
                enablePlaceholders = false
            ),
            pagingSourceFactory = { StoriesPagingSource(apiService) }
        ).liveData
    }
}

